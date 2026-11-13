<!-- docs-architect-meta {"schema_version":1,"id":"REQ-2026-001","type":"requirement","title":"Modernize the circular ring library","status":"done","owners":[],"origin":{"kind":"request","ref":"openspec/changes/archive/2026-11-14-REQ-2026-001-modernize-ring/request.md"},"approval":{"kind":"request-record","ref":"openspec/changes/archive/2026-11-14-REQ-2026-001-modernize-ring/request.md"},"sources":[],"update_when":["Scope, acceptance, risk, implementation, evidence, or documentation disposition changes"],"relations":[],"dependencies":[],"acceptance":[{"id":"AC-1","text":"Modern Kotlin View with validated range, cached batched drawing, restored state and accessibility.","status":"passed","evidence":[{"kind":"log","ref":"openspec/changes/archive/2026-11-14-REQ-2026-001-modernize-ring/evidence/build.log","description":"Successful builds, publication and device tests"}]},{"id":"AC-2","text":"Stable build tools, focused device tests, simple sample, publication support and updated README.","status":"passed","evidence":[{"kind":"log","ref":"openspec/changes/archive/2026-11-14-REQ-2026-001-modernize-ring/evidence/build.log","description":"Successful builds, publication and device tests"}]}],"evidence":[{"kind":"log","ref":"openspec/changes/archive/2026-11-14-REQ-2026-001-modernize-ring/evidence/build.log","description":"Successful builds, publication and device tests"},{"kind":"log","ref":"openspec/changes/archive/2026-11-14-REQ-2026-001-modernize-ring/evidence/tests.xml","description":"Five passing instrumentation tests"}],"affected_code":["app/**","circularringpercentageview/**","*.gradle*","gradle/**","gradlew*","gradle.properties",".github/**",".gitignore",".idea/**","source/sample.png"],"affected_docs":["README.md"],"open_questions":[],"documentation_disposition":"updated","no_doc_change_scope":[],"no_doc_change_reason":null,"slug":"modernize-ring","complexity":"small","risk":{"level":"medium","drivers":["Rendering performance and public API migration"]},"retention":"summary","domains":["ring"],"spec_baselines":{"ring":null},"ephemeral_artifacts":[],"created_at":"2026-11-14T01:38:13+08:00","updated_at":"2026-11-14T01:52:20+08:00","status_changed_at":"2026-11-14T01:52:20+08:00","completed_at":"2026-11-14T01:52:20+08:00","archived_at":"2026-11-14T01:52:20+08:00","status_history":[{"status":"accepted","at":"2026-11-14T01:38:13+08:00","reason":"Change created"},{"status":"verifying","at":"2026-11-13T17:51:47.142509+00:00","reason":"Implementation and acceptance checks passed"},{"status":"done","at":"2026-11-14T01:52:20+08:00","reason":"Close completed"}],"verified_at":"2026-11-13T17:51:01.648817+00:00","verified_against":"sha256:e66e4701f4a0215c8bb011318e4dc2fef246d1f4db492a3d01ba59325202715d@openspec/changes/archive/2026-11-14-REQ-2026-001-modernize-ring/evidence/revision.json"} -->

# Modernize the circular ring library

Replace the 2015 build and Java renderer with a small Kotlin View library. Preserve the class and major Java accessors; normalize XML parameters in a documented 2.0 migration. Keep two modules and avoid Compose runtime dependencies. User authorized implementation, commit and push in request.md.

## Acceptance Criteria

- [x] `AC-1` - Modern Kotlin View with validated range, cached batched drawing, restored state and accessibility.
- [x] `AC-2` - Stable build tools, focused device tests, simple sample, publication support and updated README.

## Documentation Disposition

- Result: updated
- Affected docs: `README.md`

## History

- User authorized modernization, validation, commit and push.
- Implementation verified by builds, lint, device tests and runtime interaction.

- 2026-11-14T01:52:20+08:00 - Close completed; requirement archived.
