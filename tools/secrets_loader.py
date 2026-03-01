import os
from pathlib import Path

def load_secrets():
    """
    Loads secrets from local.properties or secrets.properties in the project root.
    Returns a dictionary of key-value pairs.
    """
    secrets = {}
    project_root = Path(__file__).parent.parent
    
    for secret_file in ['local.properties', 'secrets.properties']:
        file_path = project_root / secret_file
        if file_path.exists():
            with open(file_path, 'r', encoding='utf-8') as f:
                for line in f:
                    line = line.strip()
                    if line and not line.startswith('#') and '=' in line:
                        key, value = line.split('=', 1)
                        secrets[key.strip()] = value.strip()
    
    return secrets

def get_secret(key, default=None):
    """Gets a secret from the environment or properties files."""
    # Check environment first
    val = os.environ.get(key)
    if val:
        return val
    
    # Check properties files
    secrets = load_secrets()
    return secrets.get(key, default)
