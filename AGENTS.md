# EvolutionBrain — Agent Info

## Status
- **Main sources compile and `MasterProofMain` runs again**, and **`mvn test` passes all suites** as of the
  modernization work described below (Maven 3.9.x + JDK 17+, no parent POM, current JogAmp JOGL/GlueGen +
  JmDNS + AssertJ-Swing from Maven Central).
- Run it with: `mvn compile exec:java` (uses the `exec-maven-plugin` configured in [pom.xml](pom.xml), which
  also passes the `--add-opens java.desktop/sun.awt=ALL-UNNAMED` flag JOGL needs on modern JDKs).
- CAUTION: running the app writes frame captures to `./film/*.png`, overwriting the original 2011 demo
  images checked into git — run `git checkout -- film/` afterwards if you don't intend to update them.

## Background
- This is the source code belonging to the master thesis of Thomas Welsch (project owner) from 2011
  (see [Master-Thesis/Master–Thesis-Thomas-Welsch_2011_06_14.pdf](Master-Thesis/Master–Thesis-Thomas-Welsch_2011_06_14.pdf)).
- It is a framework to apply evolution (mutation + selection) to randomly generated neuronal networks.
- It includes a 3D visualisation (via JOGL — see `tw.master.gl3d`) for viewing brains/networks. There is also
  a `PurJava3dImplementation` which, despite the name, is a hand-rolled pure-Java/AWT 3D projection (no
  dependency on the old `javax.media.j3d` Java3D API), so no Java3D migration is actually needed.
- The code was imported as a **direct, unmodified copy** of the 2011 source tree and did not build or run
  out of the box (old Maven parent POM, old JOGL/JMDNS/FEST-Swing versions, JDK 1.6 target, etc.).
- Goal: modernize the toolchain/build/dependencies enough to run it again, **without** restructuring the
  core design (e.g. do not replace the hand-rolled neural network/mutation code with an external ML/NN
  library — the point of the thesis code is its own implementation).

## Build setup
- Maven project (`pom.xml`). The old `parent` POM (`com.spontech:base-pom`, lived in a private, now-defunct
  Spontech Maven repository) has been removed; the handful of settings actually used (encoding, JDK level)
  are now inlined directly in `pom.xml`. (A snapshot of that old parent POM and its PMD ruleset were kept
  briefly for historical reference but have since been deleted as dead weight.)
- `maven.compiler.release` is now `17` (was JDK 1.6).
- The 2011 vendored native/binary jars under [lib/arch/](lib/arch/) and [lib/arch_jars/](lib/arch_jars/)
  (JOGL, GlueGen, JOCL, NativeWindow, JMDNS, per-OS/arch) are **no longer used for the main build** — `pom.xml`
  now depends directly on current JogAmp releases from Maven Central (`org.jogamp.jogl:jogl-all:2.6.0`,
  `org.jogamp.gluegen:gluegen-rt:2.6.0`, with `natives-macosx-universal` classifier deps) and
  `org.jmdns:jmdns:3.6.3`. The old `install_foreign_jar_to_develop_repository.sh` / `install_jars_for_maven.sh`
  scripts and vendored jars are now legacy/unused; only macOS natives are wired up so far — Linux/Windows
  natives classifiers should be added when building on those platforms.
- `javax.media.opengl.*` imports in `tw.master.gl3d.OpenGL3dImplementation`/`View` were renamed to
  `com.jogamp.opengl.*` to match the current JOGL package layout (this was the only source change JOGL's API
  evolution required).
- Legacy artifacts: Eclipse project files (`.project`, `.classpath`, `.checkstyle`, `.pmd`) are still present
  (harmless, git/Maven are authoritative). The `build.xml` (Ant) and all `.svn/` directories (leftover from
  the original SVN repo) have been removed.

## Code layout (do not restructure)
- `src/main/java/tw/master/brain` — core neuron/synapse/brain data model (`Brain`, `Neuron`, `Synapse`,
  `NeuronCluster`, activation functions).
- `src/main/java/tw/master/mutation` — genotype/mutation parameters driving evolution.
- `src/main/java/tw/master/engine`, `crawler`, `tree`, `visionfield`, `math`, `remote`, `utils` — supporting
  simulation/evolution engine code.
- `src/main/java/tw/master/gl3d` — 3D visualisation abstraction with both an OpenGL (JOGL) and a pure-Java
  implementation (`OpenGL3dImplementation`, `PurJava3dImplementation`).
- `src/main/java/tw/gui` — Swing GUI widgets/panels.
- Entry points: `tw.master.MasterProofMain` (per README, the original starting point),
  `EvolutionBrainClientMain`, `EvolutionBrainServerMain`.

## Modernization plan
1. ✅ **Get it compiling standalone** — done. Parent POM removed, settings inlined, `maven.compiler.release=17`.
   No other JDK-1.6-era compile errors turned up besides a generics-inference tightening in `ZebraJTable`
   (fixed by widening the constructor's `Vector<?>` param to `Vector<? extends Vector>` to match `JTable`'s
   own constructor signature).
2. ✅ **Replace unavailable/native dependencies with modern equivalents, keeping the same abstraction seams**
   — done for JOGL/GlueGen/JMDNS (see Build setup above); `PurJava3dImplementation` turned out to need no
   migration at all (it's pure AWT/Graphics2D, not the deprecated Java3D API).
   - `jocl`/`joal`/`nativewindow` vendored deps are unused by main sources (only `nativewindow` is a
     transitive dep of `jogl-all` now) — `jocl` is only referenced by the test-only `CLInfo.java`, which now
     depends on `org.jogamp.jocl:jocl` (test scope).
   - `pom.xml` now selects the right JogAmp natives classifier (macOS/Linux amd64+aarch64/Windows amd64) via
     OS-activated Maven profiles (`natives-macos`, `natives-linux-amd64`, `natives-linux-aarch64`,
     `natives-windows`); only macOS has actually been exercised so far, the rest are best-effort.
   - `fest-swing` (1.2, dead project) has been replaced by its maintained fork **AssertJ-Swing 3.17.1**
     (`org.assertj:assertj-swing-junit`) across `src/test/java/tw/gui/annotiations/*`, `ImagePanelTest`, and
     `ActivationFunctionFactoryTest` — only import package renames and switching the old public
     `robot`/`target` fields to their `robot()`/`target()` accessor methods were needed. `mvn test` now
     passes end-to-end.
3. ✅ **Clean up the build** — dead `scm`/`distributionManagement` pointing at `develop.spontech-spine.com`
   and the `com.spontech` parent were removed from `pom.xml`; all `.svn/` directories and the unused Ant
   `build.xml` have been deleted. Eclipse project files (`.project`, `.classpath`, `.checkstyle`, `.pmd`) are
   left in place as harmless/optional IDE metadata.
4. ✅ **Verify behavior, not redesign it** — `mvn compile exec:java` launches `MasterProofMain`: the engine
   starts, loads `images/Trails.png`, and opens the Swing/JOGL 3D window without errors (only benign,
   internally-caught JOGL reflection warnings on the AWT thread, silenced by the `--add-opens` flag).
   `mvn test` now passes all suites (18 test classes, 0 failures/errors).
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
