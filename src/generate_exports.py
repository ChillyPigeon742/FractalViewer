import os
import re
from pathlib import Path

src_dir = Path.cwd()
base_package_path = src_dir / "net" / "alek" / "buttonclicker"
base_package = "net.alek.buttonclicker"

package_exports = set()
for root, dirs, files in os.walk(base_package_path):
    if any(file.endswith(".java") for file in files):
        relative_path = Path(root).relative_to(src_dir)
        package = ".".join(relative_path.parts)
        package_exports.add(package)

sorted_exports = sorted(package_exports)
export_lines = [f"    exports {pkg};" for pkg in sorted_exports]
export_block = "\n".join(export_lines)

module_info_path = src_dir / "module-info.java"
if module_info_path.exists():
    with open(module_info_path, "r", encoding="utf-8") as f:
        content = f.read()

    cleaned = re.sub(r"^\s*exports\s+[\w\.]+;\s*$", "", content, flags=re.MULTILINE).strip()

    if cleaned.endswith("}"):
        cleaned = re.sub(r"\s*\n*\s*}$", "\n}", cleaned)
        cleaned = cleaned[:-1] + "\n" + export_block + "\n}"

    with open(module_info_path, "w", encoding="utf-8") as f:
        f.write(cleaned)