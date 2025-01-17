# README

## Overview

**AWS Credentials**  
   Ensure you have valid AWS credentials set in your `~/.aws/credentials` (or equivalent) so Terraform can authenticate properly.
This project contains two main configurations:

1. **Local Testing**: Located in the `terraform_test_local` folder. This is just to test the code locally.
2. **Final Cloud Deployment**: Located in the `terraform_final` folder. This is the final project.

Below are the detailed steps for each environment.

---

## Local Testing

1. **Navigate to the `terraform_test_local` folder**:

   ```bash
   cd terraform_test_local
   ```

2. **Initialize, plan, and apply the Terraform configuration**:

   ```bash
   terraform init
   terraform plan
   terraform apply
   ```
3. **Run the code (locally)**
4.  **Accessing the API**  
   - **Locally**:  
     ```
     http://localhost:8080/swagger-ui/index.html#/graph-api-controller
     ```
   - **Via EC2**:  
     ```
     http://<EC2_PUBLIC_IP_API>:8080/swagger-ui/index.html#/graph-api-controller
     ```

> **Important:** In this test configuration, the MongoDB EC2 instance is publicly exposed. This is **not recommended** for production, as anyone could potentially manipulate the datamart. Use this setup **only** for local testing and verification.

---

## Final Cloud Deployment

1. **Navigate to the `terraform_final` folder**:

   ```bash
   cd terraform_final
   ```

2. **Initialize, plan, and apply the Terraform configuration**:

   ```bash
   terraform init
   terraform plan
   terraform apply
   ```

3. **Accessing the API**  
   After the deployment completes, the API will be available at:
   ```
   http://<EC2_PUBLIC_IP_API>:8080/swagger-ui/index.html#/graph-api-controller
   ```
