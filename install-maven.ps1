# Script PowerShell pour installer Maven
Write-Host "Installation de Maven..." -ForegroundColor Green

# Créer le répertoire d'installation
$installDir = "C:\tools\maven"
if (!(Test-Path $installDir)) {
    New-Item -ItemType Directory -Path $installDir -Force
    Write-Host "Répertoire créé: $installDir" -ForegroundColor Yellow
}

# URL de téléchargement Maven
$mavenUrl = "https://dlcdn.apache.org/maven/maven-3/3.9.10/binaries/apache-maven-3.9.10-bin.zip"
$zipFile = "$env:TEMP\apache-maven-3.9.10-bin.zip"

# Télécharger Maven
Write-Host "Téléchargement de Maven depuis $mavenUrl..." -ForegroundColor Yellow
try {
    Invoke-WebRequest -Uri $mavenUrl -OutFile $zipFile -UseBasicParsing
    Write-Host "Téléchargement terminé." -ForegroundColor Green
} catch {
    Write-Host "Erreur lors du téléchargement: $_" -ForegroundColor Red
    exit 1
}

# Extraire l'archive
Write-Host "Extraction de l'archive..." -ForegroundColor Yellow
try {
    Expand-Archive -Path $zipFile -DestinationPath $installDir -Force
    Write-Host "Extraction terminée." -ForegroundColor Green
} catch {
    Write-Host "Erreur lors de l'extraction: $_" -ForegroundColor Red
    exit 1
}

# Trouver le répertoire Maven extrait
$mavenDir = Get-ChildItem -Path $installDir -Directory | Where-Object { $_.Name -like "apache-maven-*" } | Select-Object -First 1
if ($mavenDir) {
    $mavenHome = $mavenDir.FullName
    Write-Host "Maven installé dans: $mavenHome" -ForegroundColor Green
} else {
    Write-Host "Erreur: Répertoire Maven non trouvé après extraction." -ForegroundColor Red
    exit 1
}

# Configurer les variables d'environnement
Write-Host "Configuration des variables d'environnement..." -ForegroundColor Yellow

# MAVEN_HOME
[Environment]::SetEnvironmentVariable("MAVEN_HOME", $mavenHome, [EnvironmentVariableTarget]::User)
Write-Host "MAVEN_HOME défini: $mavenHome" -ForegroundColor Green

# Ajouter Maven au PATH
$currentPath = [Environment]::GetEnvironmentVariable("PATH", [EnvironmentVariableTarget]::User)
$mavenBin = "$mavenHome\bin"

if ($currentPath -notlike "*$mavenBin*") {
    $newPath = "$currentPath;$mavenBin"
    [Environment]::SetEnvironmentVariable("PATH", $newPath, [EnvironmentVariableTarget]::User)
    Write-Host "Maven ajouté au PATH: $mavenBin" -ForegroundColor Green
} else {
    Write-Host "Maven déjà présent dans le PATH." -ForegroundColor Yellow
}

# Nettoyer le fichier temporaire
Remove-Item $zipFile -Force -ErrorAction SilentlyContinue

Write-Host "`nInstallation terminée!" -ForegroundColor Green
Write-Host "Redémarrez votre terminal ou exécutez:" -ForegroundColor Yellow
Write-Host "`$env:PATH = [System.Environment]::GetEnvironmentVariable('PATH','User')" -ForegroundColor Cyan
Write-Host "Puis testez avec: mvn --version" -ForegroundColor Cyan
