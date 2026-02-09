# Copilot / AI Agent Instructions — cv (Spring Boot CV app)

Quick goal
- Help contributors make small, focused changes to this Spring Boot + Thymeleaf CV app.

Big picture
- This is a Spring Boot MVC application (see [src/main/java/curriculum_vitae/cv/CvApplication.java](src/main/java/curriculum_vitae/cv/CvApplication.java#L1)).
- Controllers render Thymeleaf templates from `src/main/resources/templates` and serve static assets from `src/main/resources/static`.
- Data is modeled with JPA entities under `src/main/java/.../model` and persisted via Spring Data repositories in `src/main/java/.../repository`.

Important integration points & conventions
- Templates use Thymeleaf binding to nested object graphs. Examples:
  - `th:field="*{user.name}"` binds to `Curriculum.user.name` (see `templates/curriculums/create.html`).
  - Collections are bound by index, e.g. `*{projects[__${iterStat.index}__].projectName}`.
- Entities commonly use `cascade = CascadeType.ALL` and `orphanRemoval = true` for child lists (see `src/main/java/curriculum_vitae/cv/model/Curriculum.java`).
- Repositories extend `JpaRepository`; custom finder example: `CurriculumRepository.findByUserName(String)`.

Platform-specific detail
- The PDF generator uses a local headless Chrome executable in `PdfController`:
  - Default Windows path: `C:\\Programmi\\Google\\Chrome\\Application\\chrome.exe`.
  - If you run locally, ensure Chrome exists at that path or change `chromePath` in `src/main/java/curriculum_vitae/cv/controller/PdfController.java`.
  - Endpoint: `GET /genera-pdf` prints `/` to PDF via headless Chrome.

Build / run / test
- Uses Maven wrapper present in the repo root. On Windows run:

```bash
mvnw.cmd spring-boot:run
```

- Or with the wrapper on *nix:

```bash
./mvnw spring-boot:run
```

- Run tests:

```bash
mvnw.cmd test
```

Project-specific notes for changes
- Add DB credentials or change datasource in `src/main/resources/application.properties` (currently minimal).
- Templates contain hard-coded sample data (Italian content). When modifying templates, keep `th:` attributes and `sec:` namespace (Thymeleaf + Spring Security extras) intact.
- Static CSS and JS live under `src/main/resources/static` (`/css/print_style.css` formats the printable CV).

Common PR / change patterns
- Small UI tweaks: update the Thymeleaf template under `src/main/resources/templates/` and adjust corresponding controller if a new view route is needed.
- Data model changes: update the JPA entity, repository, and any forms (`th:field`) that bind to changed properties.
- When modifying list bindings (skills/projects/educations), preserve indexed `th:each` usage so Spring MVC can bind back to lists.

What to watch for / pitfalls
- Headless PDF requires Chrome installed and reachable — local runs will fail if absent.
- Java version declared in `pom.xml` is `25` — ensure local JDK is compatible or update `pom.xml` and CI accordingly.
- No DB credentials are committed; tests use the default embedded test config. Configure MySQL if you need to run against production DB (pom.xml includes `mysql-connector-j`).

If you need more
- Ask for concrete examples (e.g., "show how to add a Skill entity and bind it to the create form").
- If you'd like, I can also add a short CONTRIBUTING or README section with the exact run/debug steps for Windows.

---
Please review and tell me which sections need more details or examples.
