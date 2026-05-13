$counts = @{}
$start = Get-Date

for ($i = 1; $i -le 100; $i++) {
    $response = Invoke-RestMethod -Uri "http://localhost:8082/test/promotion-instance" -Method Get
    $port = $response.port

    if ($counts.ContainsKey($port)) {
        $counts[$port] = $counts[$port] + 1
    } else {
        $counts[$port] = 1
    }
}

$end = Get-Date
$duration = ($end - $start).TotalMilliseconds

Write-Host "Rezultati load balancing testa:"
$counts.GetEnumerator() | Sort-Object Name | ForEach-Object {
    Write-Host "Promotion instanca na portu $($_.Name): $($_.Value) zahtjeva"
}

Write-Host "Ukupno vrijeme sa load balancingom: $duration ms"