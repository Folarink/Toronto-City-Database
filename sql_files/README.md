**Schema Apply Instructions**

- **SQL File:** `SQL FILE\create_tables_from_untitled1.sql` — DDL taken from the attachment (Untitled-1).
- **Helper script:** `SQL FILE\apply_schema.ps1` — PowerShell wrapper that calls `sqlcmd` with the connection details used earlier. Edit credentials if needed.

Running options:

- Using the provided PowerShell helper (recommended if you have `sqlcmd`):

```powershell
cd "c:\Users\Khadijat\OneDrive - University of Manitoba\Documents\COMP 3000 LEVEL\COMP 3380\Group Project\Present Cleaned Data"
./"SQL FILE"/apply_schema.ps1
```

- Or run `sqlcmd` directly:

```powershell
sqlcmd -S uranium.cs.umanitoba.ca -d cs3380 -U folarink -P 7966826 -i "SQL FILE\create_tables_from_untitled1.sql"
```

Notes & warnings:
- These CREATE TABLE statements will fail if the target tables already exist. If you want to recreate the tables from scratch, either drop them first or modify the SQL to include `IF OBJECT_ID(...) IS NOT NULL DROP TABLE ...` statements.
- Running the script will affect the remote `cs3380` database. Make sure you have a backup or are permitted to make schema changes.
- If you prefer minimal changes, consider applying only the specific `ALTER TABLE` statements to add missing columns (for example, add `host_since` and `host_identity_verified` to `hosts`) instead of re-creating tables.
