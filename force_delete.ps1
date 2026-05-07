# PowerShell script to force delete duplicate AdminController files
$controllerPath = "c:\Users\VINAYAK CHINNARATHOD\Documents\Bank System Website\backend\src\main\java\com\bank\securebank\controller"

Write-Host "Attempting to delete duplicate AdminController files..."

# Try to delete the duplicate files
try {
    Remove-Item -Path "$controllerPath\AdminController-FIXED.java" -Force -ErrorAction Stop
    Write-Host "Deleted AdminController-FIXED.java" -ForegroundColor Green
} catch {
    Write-Host "Failed to delete AdminController-FIXED.java: $($_.Exception.Message)" -ForegroundColor Red
}

try {
    Remove-Item -Path "$controllerPath\AdminController-SIMPLE.java" -Force -ErrorAction Stop
    Write-Host "Deleted AdminController-SIMPLE.java" -ForegroundColor Green
} catch {
    Write-Host "Failed to delete AdminController-SIMPLE.java: $($_.Exception.Message)" -ForegroundColor Red
}

# List remaining files
Write-Host "`nRemaining files in controller directory:" -ForegroundColor Yellow
Get-ChildItem -Path $controllerPath -Filter "*.java" | Select-Object Name
