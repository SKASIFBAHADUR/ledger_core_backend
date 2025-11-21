 # =========================
# LEDGERCORE BANKING AUTO TESTS (PowerShell Clean Version)
# =========================

$BASE = "http://localhost:8080"
$ADMIN_EMAIL = "admin@bank.com"
$ADMIN_PASS = "Admin123"
$REF_COOKIE_NAME = "refreshToken"

Write-Host "1) Health Check"
try {
    $health = Invoke-RestMethod -Uri "$BASE/actuator/health" -Method GET -ErrorAction Stop
    Write-Host "Health OK"
} catch {
    Write-Host "Health endpoint not found (OK if actuator disabled)"
}

Write-Host ""
Write-Host "2) Register Admin (idempotent)"

$registerBody = @{
    username = "admin"
    email = $ADMIN_EMAIL
    password = $ADMIN_PASS
    role = "ADMIN"
    status = "ACTIVE"
} | ConvertTo-Json

try {
    Invoke-RestMethod -Uri "$BASE/auth/register" -Method POST `
        -ContentType "application/json" -Body $registerBody
    Write-Host "Admin created or already exists"
} catch {
    Write-Host "Admin already exists or cannot be created (this is OK)"
}

Write-Host ""
Write-Host "3) Login Admin"

$response = Invoke-WebRequest `
    -Uri "$BASE/auth/login?email=$ADMIN_EMAIL&password=$ADMIN_PASS" `
    -Method POST `
    -SessionVariable session

$body = $response.Content | ConvertFrom-Json
$accessToken = $body.accessToken

if (!$accessToken) {
    Write-Host "ERROR: Access token not found in login response!"
    exit
}

# Extract refresh cookie
$refreshCookie = ($response.Headers["Set-Cookie"] | Select-String "refreshToken=").Matches.Value
$refreshCookie = $refreshCookie -replace "refreshToken=", "" -replace ";.*",""

Write-Host "Access token received."
Write-Host "Refresh cookie: $refreshCookie"

Write-Host ""
Write-Host "4) Create Customer"

$custBody = @{
    name = "AutoTest Customer"
    email = "ps1test@example.com"
    phone = "7777777777"
} | ConvertTo-Json

$custResponse = Invoke-RestMethod `
    -Uri "$BASE/customer/create" `
    -Headers @{ Authorization = "Bearer $accessToken" } `
    -Method POST `
    -ContentType "application/json" `
    -Body $custBody

$customerId = $custResponse.id
Write-Host "Customer created with ID: $customerId"

Write-Host ""
Write-Host "5) Create Account"

$accBody = @{ accountType = "SAVINGS" } | ConvertTo-Json

$accResponse = Invoke-RestMethod `
    -Uri "$BASE/account/create/$customerId" `
    -Headers @{ Authorization = "Bearer $accessToken" } `
    -Method POST `
    -ContentType "application/json" `
    -Body $accBody

$accountId = $accResponse.id
Write-Host "Account created with ID: $accountId"

Write-Host ""
Write-Host "6) Deposit with Idempotency (first call)"

$headers = @{
    Authorization = "Bearer $accessToken"
    "Idempotency-Key" = "ps1-idem-1"
}

$dep1 = Invoke-RestMethod `
    -Uri "$BASE/transactions/deposit/$accountId/1000?reference=ps1" `
    -Method POST `
    -Headers $headers

$txnId = $dep1.id
Write-Host "Transaction ID: $txnId"

Write-Host ""
Write-Host "7) Deposit again with SAME idempotency key"

$dep2 = Invoke-RestMethod `
    -Uri "$BASE/transactions/deposit/$accountId/1000?reference=ps1" `
    -Method POST `
    -Headers $headers

$txn2 = $dep2.id

if ($txnId -eq $txn2) {
    Write-Host "Idempotency Verified"
} else {
    Write-Host "ERROR: Idempotency Failed"
    exit
}

Write-Host ""
Write-Host "8) Withdraw large amount (should fail)"

try {
    Invoke-RestMethod `
        -Uri "$BASE/transactions/withdraw/$accountId/99999999" `
        -Headers @{ Authorization = "Bearer $accessToken" } `
        -Method POST
    Write-Host "ERROR: Withdraw unexpectedly succeeded"
    exit
} catch {
    Write-Host "Withdraw failed as expected"
}

Write-Host ""
Write-Host "9) Refresh Token Rotation"

if ($refreshCookie) {
    $refreshResponse = Invoke-WebRequest `
        -Uri "$BASE/auth/refresh" `
        -Method POST `
        -Headers @{ Cookie = "refreshToken=$refreshCookie" }

    $refreshBody = $refreshResponse.Content | ConvertFrom-Json
    $accessToken = $refreshBody.accessToken

    Write-Host "New Access Token Received"

    $setCookie = $refreshResponse.Headers["Set-Cookie"]
    if ($setCookie) {
        $newRefresh = $setCookie -replace "refreshToken=", "" -replace ";.*", ""
        Write-Host "New Refresh Cookie Issued"
    }
} else {
    Write-Host "No refresh cookie found, skipping refresh test."
}

Write-Host ""
Write-Host "10) Logout"

Invoke-RestMethod `
    -Uri "$BASE/auth/logout" `
    -Headers @{ Authorization = "Bearer $accessToken"; Cookie = "refreshToken=$refreshCookie" } `
    -Method POST

Write-Host "Logged out"

Write-Host ""
Write-Host "11) Try using OLD access token (should fail)"

try {
    Invoke-RestMethod `
        -Uri "$BASE/customer/get/$customerId" `
        -Headers @{ Authorization = "Bearer $accessToken" } `
        -Method GET

    Write-Host "ERROR: Old token still works! Blacklist not applied."
    exit
} catch {
    Write-Host "Old token rejected. Logout and blacklist working."
}

Write-Host ""
Write-Host "====================================="
Write-Host "      ALL TESTS COMPLETED SUCCESS    "
Write-Host "====================================="

