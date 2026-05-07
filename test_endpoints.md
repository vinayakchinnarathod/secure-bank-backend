# Database Viewer Endpoints Test

## Test these endpoints in browser or Postman:

### 1. Users Endpoint
```
GET http://localhost:8080/api/admin/users
Headers: Authorization: Bearer <JWT_TOKEN>
```

### 2. KYC Endpoint  
```
GET http://localhost:8080/api/admin/kyc
Headers: Authorization: Bearer <JWT_TOKEN>
```

### 3. Checkbook Requests Endpoint
```
GET http://localhost:8080/api/admin/checkbook-requests
Headers: Authorization: Bearer <JWT_TOKEN>
```

### 4. Loans Endpoint
```
GET http://localhost:8080/api/admin/loans
Headers: Authorization: Bearer <JWT_TOKEN>
```

### 5. Transactions Endpoint
```
GET http://localhost:8080/api/transactions?username=john_doe
Headers: Authorization: Bearer <JWT_TOKEN>
```

## Get JWT Token:
1. Login as admin: POST http://localhost:8080/api/auth/login
2. Body: {"username":"admin","password":"admin123"}
3. Copy token from response

## Expected Responses:
- Users: Array of user objects with id, username, role
- KYC: Array of KYC data with real aadhaar, pan, address
- Checkbooks: Array of checkbook requests
- Loans: Array of loan applications
- Transactions: Array of transaction data
