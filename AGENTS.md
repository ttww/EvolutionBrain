# EvolutionBrain — Agent Info

## Background
- This is the source code belonging to the master thesis of Thomas Welsch (project owner) from 2011
  (see [Master-Thesis/Master–Thesis-Thomas-Welsch_2011_06_14.pdf](Master-Thesis/Master–Thesis-Thomas-Welsch_2011_06_14.pdf)).
- It is a framework to apply evolution (mutation + selection) to randomly generated neuronal networks.
- It includes a 3D visualisation (via JOGL/Java3D — see `tw.master.gl3d`) for viewing brains/networks.
- The code was imported as a **direct, unmodified copy** of the 2011 source tree. It will **not** build or
  run out of the box today (old Maven parent POM, old JOGL/JMDNS/FEST-Swing versions, JDK 1.6 target, etc.).
- Goal going forward: modernize the toolchain/build/dependencies enough to run it again, **without**
  restructuring the core design (e.g. do not replace the hand-rolled neural network/mutation code with an
  external ML/NN library — the point of the thesis code is its own implementation).

## Build setup
- Maven project (`pom.xml`) with a `parent` POM (`com.spontech:base-pom`) that originally lived in a private
  Spontech Maven repository and is **not published anywhere accessible now**.
- A snapshot of that parent POM is checked into this repo as [spontech_parent_pom.xml](spontech_parent_pom.xml)
  (version 1.5.7) for reference — `pom.xml` currently depends on version 1.5.5 of it, which we don't have.
- Native/binary dependencies (JOGL, GlueGen, JOCL, NativeWindow, JMDNS, per-OS/arch natives) are vendored as
  jars under [lib/arch/](lib/arch/) and [lib/arch_jars/](lib/arch_jars/), installed into the local Maven repo
  via `install_foreign_jar_to_develop_repository.sh` / `install_jars_for_maven.sh` (see [pom.xml](pom.xml)
  dependencies with `groupId=com.EvolutionBrain`).
- Legacy artifacts also present: `build.xml` (Ant), Eclipse project files (`.project`, `.classpath`,
  `.checkstyle`, `.pmd`), and old Subversion metadata (`.svn/` folders — leftover from the original SVN repo,
  now superseded by this git repo).

## Code layout (do not restructure)
- `src/main/java/tw/master/brain` — core neuron/synapse/brain data model (`Brain`, `Neuron`, `Synapse`,
  `NeuronCluster`, activation functions).
- `src/main/java/tw/master/mutation` — genotype/mutation parameters driving evolution.
- `src/main/java/tw/master/engine`, `crawler`, `tree`, `visionfield`, `math`, `remote`, `utils` — supporting
  simulation/evolution engine code.
- `src/main/java/tw/master/gl3d` — 3D visualisation abstraction with both an OpenGL (JOGL) and a Java3D
  implementation (`OpenGL3dImplementation`, `PurJava3dImplementation`).
- `src/main/java/tw/gui` — Swing GUI widgets/panels.
- Entry points: `tw.master.MasterProofMain` (per README, the original starting point),
  `EvolutionBrainClientMain`, `EvolutionBrainServerMain`.

## Modernization plan
1. **Get it compiling standalone**
   - Inline/replace the missing parent POM: either vendor `spontech_parent_pom.xml` as the parent, or strip
     the dependency on it and pull the handful of settings we actually need (encoding, JDK level, a couple of
     reporting plugins) directly into `pom.xml`.
   - Bump `targetJdk`/`maven.compiler.source/target` from 1.6 to a current LTS (17 or 21) and fix any
     resulting compile errors (removed APIs, `Object.finalize`, threading APIs, etc.) without changing logic.
2. **Replace unavailable/native dependencies with modern equivalents, keeping the same abstraction seams**
   - JOGL/GlueGen/JOCL/NativeWindow (2011 vintage, macOS 32/64-bit "universal" natives, no Apple Silicon
     support) → migrate to a current JOGL 2.x release (available on Maven Central) which already handles
     multi-arch natives without the manual per-OS jar vendoring.
   - Since the code already has a `World3dInterface`/`World3dDrawInterface` abstraction with separate JOGL and
     Java3D implementations, keep that seam and just update `OpenGL3dImplementation` to the new JOGL 2.x API
     (package/class names changed since 2011); Java3D branch can likely be dropped or kept behind the same
     interface if still useful.
   - `jmdns` → current version available on Maven Central, should be close to drop-in.
   - `fest-swing` (1.2, dead project) → only used for tests; either drop or replace with AssertJ-Swing (the
     maintained fork with a compatible API) if the tests are worth keeping.
3. **Clean up the build**
   - Remove/ignore leftover `.svn` directories and Eclipse-specific files, or leave them but don't treat them
     as source of truth (git is now authoritative).
   - Drop the `build.xml` (Ant) path if Maven is the build system of record, or note it as legacy/unused.
   - Re-point `distributionManagement`/`scm` sections (currently point at defunct
     `develop.spontech-spine.com`) or remove them if no longer applicable.
4. **Verify behavior, not redesign it**
   - Once it compiles and the 3D window opens, use `MasterProofMain` to confirm the simulation/evolution loop
     still produces the same behavior as before.
   - Add/keep JUnit tests (already present under `src/test/java`) as a regression net while updating
     dependencies, rather than rewriting the neural network/mutation algorithms themselves.
5. **Explicitly out of scope**
   - Do not swap the custom `Brain`/`Neuron`/`Synapse` model for an external neural-network library (e.g.
     DL4J, Deeplearning libs) — the thesis's value is in its own implementation.
   - Do not restructure packages/classes beyond what's needed to compile/run on modern JDK + JOGL.
6. **Website redesign**
   - The old project site under [evolutionbrain_web_site/evolutionbrain.sourceforge.net/](evolutionbrain_web_site/evolutionbrain.sourceforge.net/)
     is 2011-era static HTML/CSS (SourceForge-hosted look & feel) and needs a complete redesign with current
     styles/structure (responsive layout, modern CSS, updated content) rather than incremental patching.
   - Content (project description, screenshots, thesis link, downloads) should be preserved/migrated, but the
     markup/styling should be rebuilt from scratch.
7. **Media asset conversion**
   - The `.mov` files under [film/](film/) (e.g. `AAAA_ClientImage.png.png.mov`) are legacy QuickTime
     captures and should be converted to a modern, widely-supported format/codec (e.g. H.264/H.265 MP4 or
     WebM) for smaller size and better compatibility with today's browsers/players.

## Repo hygiene reminder
- After creating or editing files in this repo, stage (`git add`) and commit/push regularly instead of
  batching up large uncommitted changesets.
