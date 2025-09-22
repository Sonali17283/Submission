# Hiring App (Spring Boot)

A Spring Boot application that, on startup, calls a webhook generation API, determines your assigned SQL question by your registration number, and submits your final SQL answer to the returned webhook URL using a JWT in the Authorization header.

## What it does
- On startup, POSTs to `/hiring/generateWebhook/JAVA` with your name, regNo, and email.
- Receives a `webhook` URL and an `accessToken` (JWT).
- Determines your question based on the last two digits of `regNo`:
  - Odd  → use `src/main/resources/queries/question1.sql`
  - Even → use `src/main/resources/queries/question2.sql`
- Reads the SQL from the selected file and POSTs it to the `webhook` URL as `{ "finalQuery": "..." }` with header `Authorization: Bearer <accessToken>`.

## Requirements satisfied
- Uses Spring Boot with `RestTemplate`.
- Flow is triggered automatically on application startup (no controller/endpoints).
- Uses JWT in the Authorization header for the second API call.

## Configure
Edit `src/main/resources/application.yml`:
```yaml
candidate:
  name: YOUR_NAME_HERE
  regNo: REG12347    # update to your actual reg no
  email: your.email@example.com
```

Put your final SQL query in the appropriate file:
- `src/main/resources/queries/question1.sql` for odd last-two regNo.
- `src/main/resources/queries/question2.sql` for even last-two regNo.

## Build
```bash
mvn -v            # ensure Maven is installed
mvn clean package # builds the fat JAR
```
The output JAR will be at `target/hiring-app-0.0.1-SNAPSHOT.jar`.

## Run
```bash
java -jar target/hiring-app-0.0.1-SNAPSHOT.jar
```
The app will log progress and whether the submission succeeded.

## Submission checklist (as per instructions)
- Code pushed to a public GitHub repo.
- Final JAR output available.
- RAW downloadable link to the JAR.

### Recommended GitHub steps
1. Create a new public repository and push this project.
2. Build locally with `mvn clean package`.
3. Create a GitHub Release and upload the generated JAR `target/hiring-app-0.0.1-SNAPSHOT.jar` as an asset.
4. Copy the direct download URL for the asset. It looks like:
   - `https://github.com/<user>/<repo>/releases/download/<tag>/hiring-app-0.0.1-SNAPSHOT.jar`
5. Provide in the form:
   - GitHub repo URL: `https://github.com/<user>/<repo>.git`
   - Public JAR download URL: the link from step 4.

### Optional: CI build (already configured)
A GitHub Actions workflow at `.github/workflows/build.yml` builds the JAR on every push and attaches it to a Release when a tag is pushed. To trigger:
- `git tag v0.1.0 && git push --tags`
- A Release will be created with the JAR attached for a raw download link.

## Notes
- API base URL can be overridden via `hiring.baseUrl` property if needed.
- Logs include helpful guidance if the SQL file is empty.
