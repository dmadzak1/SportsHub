$start = Get-Date

for ($i = 1; $i -le 100; $i++) {
    Invoke-RestMethod -Uri "http://localhost:8083/instance" -Method Get | Out-Null
}

$end = Get-Date
$duration = ($end - $start).TotalMilliseconds

Write-Host "Rezultati testa bez load balancinga:"
Write-Host "Promotion instanca na portu 8083: 100 zahtjeva"
Write-Host "Promotion instanca na portu 8084: 0 zahtjeva"
Write-Host "Ukupno vrijeme bez load balancinga: $duration ms"