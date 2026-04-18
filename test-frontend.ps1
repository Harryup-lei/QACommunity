$r = Invoke-WebRequest -Uri 'http://localhost:5173' -UseBasicParsing -TimeoutSec 5
Write-Host "Status:" $r.StatusCode
Write-Host "Content:" $r.Content.Substring(0, [Math]::Min(500, $r.Content.Length))