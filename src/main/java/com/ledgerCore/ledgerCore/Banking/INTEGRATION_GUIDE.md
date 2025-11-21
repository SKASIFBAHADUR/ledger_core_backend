# Frontend-Backend Integration Guide

## Overview
This document describes the complete integration between the React frontend and Spring Boot backend for the LedgerCore Banking System.

## Backend API Endpoints

### Authentication (`/auth`)
- **POST** `/auth/login` - Login with email and password (form-urlencoded)
- **POST** `/auth/register` - Register new user (JSON body)
- **POST** `/auth/refresh` - Refresh access token using refresh token cookie
- **POST** `/auth/logout` - Logout and invalidate tokens

### Customers (`/customer`)
- **GET** `/customer/list` - Get all customers
- **POST** `/customer/create` - Create new customer
- **GET** `/customer/get/{id}` - Get customer by ID
- **PUT** `/customer/update/{id}` - Update customer
- **DELETE** `/customer/delete/{id}` - Delete customer

### Accounts (`/account`)
- **GET** `/account/list` - Get all accounts
- **POST** `/account/create/{customerId}` - Create account for customer
- **GET** `/account/get/{accountId}` - Get account by ID
- **DELETE** `/account/delete/{accountId}` - Delete account

### Transactions (`/transactions`)
- **POST** `/transactions/deposit/{accountId}/{amount}?reference=...` - Deposit money
- **POST** `/transactions/withdraw/{accountId}/{amount}?reference=...` - Withdraw money
- **POST** `/transactions/transfer/{fromAccountId}/{toAccountId}/{amount}?reference=...` - Transfer money
- All transaction endpoints support `Idempotency-Key` header

### Ledger (`/ledger`)
- **GET** `/ledger/account/{accNo}` - Get ledger entries by account ID
- **GET** `/ledger/reference/{ref}` - Get ledger entries by transaction ID

## Frontend Configuration

### API Base URL
- Default: `http://localhost:8080`
- Configured in: `frontend/src/services/api.js`

### Authentication Flow
1. User logs in via `/auth/login`
2. Backend returns access token in response body
3. Refresh token is stored in httpOnly cookie
4. Access token is stored in localStorage
5. Axios interceptor adds Bearer token to all requests
6. On 401 error, frontend automatically attempts token refresh

### CORS Configuration
- Backend allows: `http://localhost:3000` and `http://127.0.0.1:3000`
- Credentials: Enabled
- Methods: GET, POST, PUT, DELETE, OPTIONS, PATCH

## Data Models

### Customer
```json
{
  "id": 1,
  "name": "John Doe",
  "email": "john@example.com",
  "phone": "1234567890"
}
```

### Account
```json
{
  "id": "uuid-string",
  "accountNumber": "AC1234567890",
  "balance": 1000.00,
  "accountType": "SAVINGS",
  "createdAt": "2024-01-01T00:00:00"
}
```

### TransactionRecord
```json
{
  "id": "uuid-string",
  "type": "DEPOSIT",
  "amount": 100.00,
  "reference": "optional-reference",
  "status": "SUCCESS",
  "createdAt": "2024-01-01T00:00:00"
}
```

### LedgerEntry
```json
{
  "id": "uuid-string",
  "transactionId": "uuid-string",
  "accountId": "uuid-string",
  "entryType": "DEBIT",
  "amount": 100.00,
  "description": "Transaction description",
  "createdAt": "2024-01-01T00:00:00"
}
```

## Key Integration Points

### 1. Authentication
- Frontend sends login credentials as form-urlencoded
- Backend sets refresh token as httpOnly cookie
- Access token stored in localStorage
- Automatic token refresh on 401 errors

### 2. Customer Management
- Frontend fetches all customers via `/customer/list`
- Create/Update/Delete operations use standard REST patterns
- Real-time updates after mutations

### 3. Account Management
- Frontend fetches all accounts via `/account/list`
- Account creation requires customer ID in path
- Account ID is UUID string (not Long)

### 4. Transactions
- Reference parameter passed as query string
- Idempotency key generated client-side
- Transaction results displayed immediately

### 5. Ledger Viewing
- Search by account ID or transaction ID
- Field mapping: `entryType` (not `type`), `createdAt` (not `timestamp`)
- No `balanceAfter` field - shows description instead

## Error Handling

### Backend
- Returns error messages in response body
- Uses HTTP status codes appropriately
- Validation errors return 400 Bad Request

### Frontend
- Catches and displays error messages
- Shows user-friendly error messages
- Handles network errors gracefully

## Security

### Cookies
- Refresh tokens stored as httpOnly cookies
- Secure flag: `false` in development (set to `true` in production)
- SameSite: `Lax` for better compatibility

### JWT Tokens
- Access tokens in Authorization header
- Automatic refresh on expiration
- Token blacklisting on logout

## Running the Application

### Backend
1. Ensure Spring Boot application runs on port 8080
2. Database should be configured and running
3. CORS is configured for `http://localhost:3000`

### Frontend
1. Navigate to `frontend` directory
2. Run `npm install`
3. Run `npm run dev`
4. Application available at `http://localhost:3000`

## Testing the Integration

1. **Register a new user** via `/auth/register`
2. **Login** with credentials
3. **Create a customer** via Customers page
4. **Create an account** for the customer
5. **Process transactions** (deposit/withdraw/transfer)
6. **View ledger entries** by account or transaction ID

## Troubleshooting

### CORS Errors
- Ensure backend CORS allows `http://localhost:3000`
- Check that `allowCredentials(true)` is set

### 401 Unauthorized
- Check if access token is being sent in Authorization header
- Verify token refresh mechanism is working
- Check if refresh token cookie is being sent

### 404 Not Found
- Verify backend is running on port 8080
- Check API endpoint paths match exactly
- Ensure backend controllers are properly mapped

### Cookie Issues
- In development, `secure` flag is `false`
- Ensure `withCredentials: true` in axios config
- Check browser console for cookie warnings

## Notes

- Account IDs are UUID strings, not Long integers
- Customer IDs are Long integers
- Transaction references are optional query parameters
- Ledger entries use `entryType` field (DEBIT/CREDIT)
- All dates are in ISO 8601 format

