# Project Assessment

An honest assessment of EvolutionBrain — its historical context, thesis relevance, and code quality
by today's standards.

## Historical context and thesis relevance
- Developed by Thomas Welsch as the practical component of his 2011 master thesis
  ([Master-Thesis/Master–Thesis-Thomas-Welsch_2011_06_14.pdf](Master-Thesis/Master–Thesis-Thomas-Welsch_2011_06_14.pdf)),
  implementing a framework for experiments with neuronal networks driven by evolutionary
  mutation/selection, plus a 3D visualisation of the resulting brains.
- Genuinely interesting for its time: a hand-rolled evolutionary neural simulator with 3D
  visualization, client/server distribution (`EvolutionBrainClientMain`/`ServerMain`, `jmdns`
  discovery), and a real thesis behind it — a solid scope for a master's project in 2011.
- The core idea (mutate genotype parameters → build brains → select) is cleanly separated
  conceptually into `brain` (phenotype/runtime), `mutation` (genotype), `engine`/`crawler`
  (evolution loop), `gl3d` (visualization abstraction). That top-level separation is the project's
  biggest strength and is worth preserving as-is when modernizing (see [AGENTS.md](AGENTS.md)).
- Released as open source with explicit third-party permissions obtained for the icon artwork and
  one utility class (see [copyrights.md](copyrights.md)).

## Code quality, judged by today's standards
- **Public mutable fields everywhere** (`Brain.neurons`, `Neuron.x/y/z/a/c/name`, etc.) — no
  encapsulation, so anything can reach in and mutate internal state. This was a deliberate
  architectural decision, not an oversight: with the CPU/memory/JVM constraints of 2011 and
  simulations involving large numbers of neurons/synapses evolving over many generations, avoiding
  getter/setter indirection and object-per-value overhead was a meaningful way to keep simulation
  speed and memory footprint down. This pattern shows up by purpose in many places for that reason.
- **Static mutable "global" tuning knobs** (`Neuron.selfStimulationFactor`, `distanceFactor`,
  `rechargePerRun`, `maxChargeValue` as `static` fields) — all neurons in the JVM share one global
  config, which makes multi-brain/parallel experiments and testing fragile (shared, non-thread-safe
  global state instead of per-brain config). This trades flexibility for the same
  performance/memory rationale as above, but is worth revisiting now that concurrent
  multi-experiment runs and thread safety matter more than they did in 2011.
- **God-object tendency**: `Brain` implements a GL drawing interface (`World3dDrawInterface`)
  directly on the domain model, mixing simulation logic, name-lookup maps, sound utils, and
  visualization concerns in one class. Domain model and rendering are coupled rather than composed.
- **Serializable domain objects** used for save/load — workable, but brittle for anything
  long-lived (version skew, no schema evolution).
- Sparse Javadoc/comments, `System.err.println` debug leftovers, inconsistent formatting — normal
  for the era/tooling (Checkstyle/PMD were only just being introduced per the original README TODOs).
- Test coverage is present (`src/test/java`) but likely thin relative to the surface area — worth
  checking coverage before relying on tests as a full regression net.

## Build/toolchain state (the bigger practical problem)
- Currently **not buildable at all**: missing parent POM, JDK 1.6 target, 2011-vintage native
  JOGL/JMDNS/FEST jars vendored per-OS (no Apple Silicon support), dead
  `distributionManagement`/`scm` URLs. This is a bigger blocker to "bringing it to life" than the
  code quality itself.

## Overall verdict
The **architecture/domain separation is sound and worth keeping** exactly as designed (no swapping
in an ML/NN library — that would destroy the point of the thesis). The **implementation style is
dated but deliberate** — public fields and static shared state were a conscious performance/memory
trade-off given 2011-era hardware and large-scale simulations, not sloppiness. The **real work in
reviving this project is mostly toolchain/build resurrection** (see the modernization plan in
[AGENTS.md](AGENTS.md)), with only modest, surgical hardening needed afterwards (e.g. making the
static neuron constants instance-level if running multiple independent experiments concurrently
becomes a goal) — not a rewrite.
