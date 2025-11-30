<#
PowerShell helper to apply the schema SQL file to the remote SQL Server.
Usage: Open an elevated PowerShell prompt in the project root and run:
    ./"SQL FILE"/apply_schema.ps1

This script uses `sqlcmd`. If you prefer SSMS, open the SQL file there and run it.
#>

$server = "uranium.cs.umanitoba.ca"
$database = "cs3380"
$username = "folarink"
$password = "7966826"
$sqlFile = "SQL FILE\create_tables_from_untitled1.sql"

if (-not (Get-Command sqlcmd -ErrorAction SilentlyContinue)) {
    Write-Host "sqlcmd not found. Install the SQL Server Command Line Tools or run the SQL file from SSMS." -ForegroundColor Yellow
    Write-Host "You can also run the SQL manually with this command:" -ForegroundColor Yellow
    Write-Host "sqlcmd -S $server -d $database -U $username -P <password> -i \"$sqlFile\"" -ForegroundColor Cyan
    exit 1
}

Write-Host "Applying schema file: $sqlFile to server: $server, database: $database"

try {
    sqlcmd -S $server -d $database -U $username -P $password -i $sqlFile
    if ($LASTEXITCODE -eq 0) {
        Write-Host "Schema applied successfully." -ForegroundColor Green
    } else {
        Write-Host "sqlcmd returned exit code $LASTEXITCODE" -ForegroundColor Red
    }
} catch {
    Write-Host "Error running sqlcmd: $_" -ForegroundColor Red
}
