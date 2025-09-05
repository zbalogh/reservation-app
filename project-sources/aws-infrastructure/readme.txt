This project is based on AWS Java CDK to generate CloudFormation templates for deploying infrastructure on AWS.


-----------------
Stacks Included:
-----------------

1. LocalStack

    LocalStack is for local development and testing using LocalStack.


2. AWS Private Stack

    AWS Private Stack contains resources that should not be exposed to the public internet.
    Only ALB (Application Load Balancer) is exposed to the public internet in this stack.
    Recommended for production environments.


3. AWS Public Stack

    AWS Public Stack contains resources that can be accessed from the public internet.
    All services and ALB (Application Load Balancer) are exposed to the public internet in this stack.
    Not recommended in production environments.


Note: The AWS Private Stack and AWS Public Stack are deployed to your AWS account.


---------------
How to deploy:
---------------

To deploy a stack, use the following commands:

1) Open the project in your IDE (e.g IntelliJ, Eclipse).

2) Build the Maven project in your IDE

2) Generate the CloudFormation template and deploy the stack

   You can generate the CloudFormation template directly in your IDE by running the main method in the corresponding Java class.
   Then, you can deploy the generated CloudFormation template using the provided shell scripts.

 a) Deploy the LocalStack (for local testing):
    - Run the main method in LocalStack.java, the ClodFormation template will be generated in "cdk.out.local" folder.
    - Run "localstack-deploy.sh" script in the Bash terminal.

 b) Deploy the AWS Private Stack:
    - Run the main method in AwsPrivateStack.java, the ClodFormation template will be generated in "cdk.out.aws-private-nets" folder.
    - Run "aws-private-stack-deploy.sh" script in the Bash terminal.

 c) Deploy the AWS Public Stack:
    - Run the main method in AwsPublicStack.java, the ClodFormation template will be generated in "cdk.out.aws-public-nets" folder.
    - Run "aws-public-stack-deploy.sh" script in the Bash terminal.


Notes:

Installation of AWS CLI is required for deployment scripts to work.
Make sure to configure your AWS credentials and region before deploying the AWS stacks.

If you have multiple AWS profiles, you can specify the profile to use by setting the AWS_PROFILE environment variable.
You can execute the "set-aws-profile.sh" script to set the AWS_PROFILE environment variable.
