# Workflow

This document defines the default development workflow for this repository. It is intentionally tech-stack-agnostic so it stays useful as the project grows.

## Principles

- **Keep `main` always releasable**: changes land via pull requests (PRs) with review and CI checks.
- **Small, focused changes**: prefer incremental PRs that are easy to review.
- **Automate quality gates**: formatting, linting, tests, and builds should run locally and in CI.

## Branching

- **Base branch**: `main`
- **Working branches**: create a short-lived branch per change:
  - `feature/<topic>` for new functionality
  - `fix/<issue>` for bug fixes
  - `docs/<topic>` for documentation-only changes
  - `chore/<topic>` for maintenance (deps, tooling, refactors)

Example:

```bash
git checkout -b feature/add-login
```

## Commits

- **One logical change per commit** when possible.
- **Write meaningful commit messages** that explain *why* the change exists.

Suggested commit message style:

- `feat: <summary>`
- `fix: <summary>`
- `docs: <summary>`
- `chore: <summary>`
- `refactor: <summary>`
- `test: <summary>`

## Local quality checks

Before opening a PR, ensure:

- **Build** succeeds (if applicable).
- **Formatting** is applied.
- **Lint** passes.
- **Tests** pass.

If the repo later adds standard scripts (for example, `make lint`, `npm test`, `pytest`, etc.), prefer using those so local and CI behavior matches.

## Pull requests

Open a PR from your branch into `main` and include:

- **Summary**: what changed and why.
- **Scope**: what is intentionally out-of-scope.
- **Test plan**: how you validated the change (commands and/or manual steps).
- **Screenshots**: for UI changes.
- **Risk/rollout**: if the change is risky, document mitigation or rollout steps.

### Review expectations

- Keep PRs reviewable (generally “small enough to review in one sitting”).
- Resolve review comments with follow-up commits or by amending, depending on team preference.
- Avoid force-pushing after review has started unless necessary; if you do, call it out in the PR.

## CI

CI should run automatically on PRs and typically includes:

- Formatting / lint checks
- Unit/integration tests
- Build/package verification (where relevant)

If CI fails:

- Fix issues on the PR branch and re-run checks.
- Prefer fixing the root cause over suppressing warnings/errors.

## Merging

Preferred merge strategy (pick one and stay consistent):

- **Squash merge**: recommended when you want a clean `main` history (one commit per PR).
- **Rebase merge**: recommended when you want linear history while preserving commits.
- **Merge commit**: recommended when you want explicit PR boundaries in history.

Regardless of strategy:

- Do not merge without passing CI (unless there’s an explicit, documented exception).
- Ensure the PR description includes an accurate test plan.

## Releases (optional, when applicable)

If/when releases are introduced, standardize on:

- **Semantic Versioning**: `MAJOR.MINOR.PATCH`
- **Release notes**: what changed, upgrade notes, and any breaking changes
- **Tagging**: create a git tag per release (e.g., `v1.2.3`)

## Documentation updates

When changing behavior, update docs in the same PR:

- `README.md` for high-level usage and setup
- `WORKFLOW.md` for process changes

