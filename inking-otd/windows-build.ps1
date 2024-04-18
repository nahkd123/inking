#!/bin/bash pwsh 
# Write-Output (Get-FileHash LICENSE -Algorithm SHA1).Hash

$SLN = "$PSScriptRoot\Inking.Otd"
$CSPROJ = "$SLN\Inking.Otd.csproj"
$DOTNET_VERSION = "net8.0"
$DOTNET_CONFIGURATION = "Release"
$LIBNAME = "Inking.Otd.dll"

$OUTPUTROOT = "$PSScriptRoot\src\main\resources\natives"

function buildTarget($target) {
	Write-Output "Building Inking OTD bridge for $target..."
	$sourceFile = "$SLN\bin\$DOTNET_CONFIGURATION\$DOTNET_VERSION\$target\native\Inking.Otd.dll"
	$outputFile = "$OUTPUTROOT\$target\Inking.Otd.dll"
	$outputHash = "$outputFile.sha1"

	Write-Output "    Source File   = $sourceFile"
	Write-Output "    Output File   = $outputFile"
	Write-Output "    Hash File     = $outputHash"

	if (-Not (Test-Path $outputFile -PathType Leaf)) {
		Write-Output "Building $outputFile..."
		dotnet publish -r $target -c $DOTNET_CONFIGURATION $CSPROJ

		if (-Not ($?)) {
			Write-Error "Failed to build for $target"
			return
		}

		if (Test-Path $outputFile -PathType Leaf) { Remove-Item -Path $outputFile }
		New-Item -Path "$outputFile\.." -ItemType Directory -Force
		Copy-Item -Path $sourceFile -Destination $outputFile

		if (Test-Path $outputHash -PathType Leaf) { Remove-Item -Path $outputHash }
		New-Item -Path $outputHash -Value (Get-FileHash $outputFile -Algorithm SHA1).Hash -Force
	} else {
		Write-Warning "Ignoring $outputFile because it is already exists"
	}
}

Write-Output "Solution Root is $SLN"
Write-Output "C# Project File is $CSPROJ"

if ($args.Count -Eq 0) {
	Write-Output "No targets."
}

if ($args.Count -Eq 1) {
	buildTarget($args[0]);
}

if ($args.Count -Gt 1) {
	if ($PSEdition -Eq "Core") { $pwshExe = "$PSHOME\pwsh.exe" }
	else { $pwshExe = "$PSHOME\powershell.exe" }
	Write-Output "Building in parallel..."

	foreach ($target in $args) {
		Start-Process -NoNewWindow $pwshExe -ArgumentList "$($MyInvocation.InvocationName) $target" | Wait-Process 
	}
}
