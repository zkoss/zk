# Fix batch workflow

How to run one batch of Marble fixes (an NS batch from a tracking list) so that a regression in
a neighbouring component is caught in the first round instead of three batches later. The cases
behind it: a fix that broke centered Tree columns and took four more batches to repair; a
`[style*="width"]` selector that also matched `min-width`; a guard that could never fail.

Use it when a batch edits a **shared** selector or a rule several components rely on. A batch
whose selectors stay entirely inside one component's own classes can skip steps 2 and 3.

## Steps

1. **Read the scope.** Take the items from the batch's tracking list. If one has an open
   decision, raise the decision and do not start that item.
2. **Impact list, before editing.** Run from `zkpreview/`:
   `node scripts/select-tests.mjs --json <changed paths>` (see
   `zkpreview/scripts/README-select-tests.md`). For every selector the batch will change, write
   down which other components' classes it can match and which pages carry them. If the answer
   is `full`, or `uncovered` is not empty, run the full suite; do not narrow it by hand. Write
   the list into the batch's record file so the verifier can read it.
3. **Check the selector form.** No substring attribute selectors such as `[style*="width"]`
   (they also match `min-width` and `max-width`). If an attribute selector is unavoidable, match
   the whole declaration and say why a class cannot do it.
4. **Implement.** A separate engineer agent makes the change. Start it fresh; do not fork it.
5. **Verify.** A different agent from the engineer checks the pages the issue was reported on
   plus every page on the step 2 list. Follow `reference/verification.md`: at least one row per
   interactive component performs the interaction and asserts the state change, and every new
   assertion is proven to fail by a mutation.
6. **Regression → back to step 4.** Re-dispatch a failed item as a new run, not a resume.
7. **Commit locally.** Message form `ZK-6112: … (ZK-XXXX)`. Report the full SHA. Do not push.
   Raise any decision under a new D-id.
