# Agent Changelog

Log of AI-agent-assisted work on Streamly, in chronological order. Each entry is
added at the time the work happens (same commit or the one right after), not
retrofitted at the end.

Format:
```
## YYYY-MM-DD
### Agent
<agent name>

### Commit
<commit-hash or "pending">

### Task
<short task title>

### Prompt
<what was asked>

### Changes
- <bullet summary of changes>

### Files
- <files/areas touched>
```

---

## 2026-07-25
### Agent
Claude Code

### Commit
42dd94f

### Task
Configure AI workflow.

### Prompt
Create AGENTS.md as the single source of truth and configure symlinks for Claude, Cursor, and Codex.

### Changes
- Added AGENTS.md
- Added CLAUDE.md symlink
- Added .cursor/rules symlink
- Added .codex/instructions.md symlink
- Added .antigravity/rules symlink

### Files
- AGENTS.md
- CLAUDE.md
- .cursor/rules
- .codex/instructions.md
- .antigravity/rules

--------------------

## 2026-07-25
### Agent
Claude Code

### Commit
60a0b55

### Task 1
Create the agent changelog file.

### Prompt
"Create a new file at `docs/AGENTS_CHANGELOG.md`. This file will serve as a changelog for all AI-agent-assisted work on Streamly. 

### Changes
- Created docs/AGENTS_CHANGELOG.md 
- Defined the entry format/template used by this file

### Files
- docs/AGENTS_CHANGELOG.md

----------------------

### Task 2
Update AGENTS.md with additional rules.

### Prompt
"add rule for String, Colors, Previews" / "keep color code in Color file  and use through Material Theme" / " 3 preview every screen including foldable device" / "Light and Dark theme colors will always be the same." / "set features domain or data rules inside Agents.md" (added Domain/Data Rules, kept DTO/local/mapper conditional) / "add base inside core/ common/ "Add a rule that all dependencies, plugins, and versions must be managed  through gradle/libs.versions.toml"

### Changes
- Added `Strings` rule — no hardcoded user-facing text, use `stringResource()`
- Added `Colors` rule — colors defined in Color.kt/Theme.kt, accessed via `MaterialTheme.colorScheme`, light/dark schemes kept identical, dynamic color disabled
- Added `Previews` rule — 3 `@Preview`s per screen (mobile, foldable, tablet)
- Added `Domain Rules` and `Data Rules` subsections under Feature Structure — remote datasource always required; DTO/mapper/local datasource only when actually needed
- Added `core/common/base/` to the core tree and documented `MVIViewModel` base class rules
- Added `Dependencies (Gradle)` section — all deps declared via `gradle/libs.versions.toml`, no hardcoded versions in build files

### Files
- AGENTS.md
