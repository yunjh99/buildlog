# GitHub Actions CI/CD setup

BuildLog tests every push to `main` and deploys through AWS Systems Manager (SSM).
GitHub authenticates to AWS with OIDC, so no AWS access key or EC2 SSH private key is
stored in GitHub.

## 1. EC2 role

Create an IAM role named `BuildLogEc2SsmRole` with the **AWS service / EC2** trusted
entity and attach the AWS managed policy:

```text
AmazonSSMManagedInstanceCore
```

Attach the role to the BuildLog EC2 instance through **Actions > Security > Modify IAM
role**. The instance must appear as a managed node in Systems Manager before deployment.

## 2. GitHub OIDC provider

In IAM, create an OpenID Connect identity provider:

```text
Provider URL: https://token.actions.githubusercontent.com
Audience:     sts.amazonaws.com
```

## 3. GitHub deploy role

Create an IAM role named `BuildLogGitHubDeployRole`. Replace `AWS_ACCOUNT_ID` in this
trust policy with the 12-digit AWS account ID:

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Principal": {
        "Federated": "arn:aws:iam::AWS_ACCOUNT_ID:oidc-provider/token.actions.githubusercontent.com"
      },
      "Action": "sts:AssumeRoleWithWebIdentity",
      "Condition": {
        "StringEquals": {
          "token.actions.githubusercontent.com:aud": "sts.amazonaws.com",
          "token.actions.githubusercontent.com:sub": "repo:yunjh99/buildlog:ref:refs/heads/main"
        }
      }
    }
  ]
}
```

Attach the following inline policy after replacing `AWS_ACCOUNT_ID`:

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "DeployToBuildLogInstance",
      "Effect": "Allow",
      "Action": "ssm:SendCommand",
      "Resource": [
        "arn:aws:ssm:ap-northeast-2::document/AWS-RunShellScript",
        "arn:aws:ec2:ap-northeast-2:AWS_ACCOUNT_ID:instance/i-0b2420a0d9abc79c1"
      ]
    },
    {
      "Sid": "ReadDeploymentResult",
      "Effect": "Allow",
      "Action": "ssm:GetCommandInvocation",
      "Resource": "*"
    }
  ]
}
```

## 4. GitHub variables

Open **Settings > Secrets and variables > Actions > Variables** and add:

```text
AWS_DEPLOY_ROLE_ARN = arn:aws:iam::AWS_ACCOUNT_ID:role/BuildLogGitHubDeployRole
AWS_INSTANCE_ID     = i-0b2420a0d9abc79c1
```

These values are identifiers, not credentials. Do not add the EC2 `.pem`, `.env`, AWS
access keys, database passwords, or JWT secret to GitHub.

## 5. Verify

Push a commit to `main` or run **Actions > CI/CD > Run workflow**. A successful run
must complete `backend-test`, `frontend-test`, and `deploy` in that order.
