/**
 * Compara firestore.rules local con el ruleset activo en Firebase (solo lectura).
 * Exit 0 = coinciden | Exit 1 = difieren | Exit 2 = no verificable (API/auth/red)
 */
const fs = require('fs');
const path = require('path');

const PROJECT_ID = 'sancarlina-99748';
const LOCAL_RULES_PATH = path.join(__dirname, '..', 'firestore.rules');

/** Normaliza saltos de línea y espacios para comparación justa. */
function normalizeRules(content) {
  return content
    .replace(/\r\n/g, '\n')
    .trim()
    .replace(/\n{3,}/g, '\n\n');
}

/** Obtiene el contenido del ruleset Firestore activo vía Firebase Rules API. */
async function fetchActiveFirestoreRules() {
  const { requireAuth } = require('firebase-tools/lib/requireAuth');
  const rulesApi = require('firebase-tools/lib/gcp/rules');

  const options = { project: PROJECT_ID };
  await requireAuth(options);

  const releases = await rulesApi.listAllReleases(PROJECT_ID);
  const rulesetName = await rulesApi.getLatestRulesetName(
    PROJECT_ID,
    'cloud.firestore',
    releases
  );

  if (!rulesetName) {
    throw new Error('No se encontró release activo para cloud.firestore');
  }

  const files = await rulesApi.getRulesetContent(rulesetName);
  const firestoreFile = files.find((f) => f.name && f.name.includes('firestore')) || files[0];
  if (!firestoreFile || !firestoreFile.content) {
    throw new Error('Ruleset sin contenido legible');
  }

  return { content: firestoreFile.content, rulesetName };
}

/** Resumen corto de diferencias línea a línea. */
function summarizeDiff(local, remote) {
  const localLines = local.split('\n');
  const remoteLines = remote.split('\n');
  const max = Math.max(localLines.length, remoteLines.length);
  const diffs = [];

  for (let i = 0; i < max && diffs.length < 10; i++) {
    const a = localLines[i] ?? '<EOF>';
    const b = remoteLines[i] ?? '<EOF>';
    if (a !== b) {
      diffs.push(`L${i + 1} local:  ${a}`);
      diffs.push(`L${i + 1} remoto: ${b}`);
    }
  }

  return diffs.join('\n');
}

async function main() {
  if (!fs.existsSync(LOCAL_RULES_PATH)) {
    console.error('ERROR: No existe firestore.rules local');
    process.exit(2);
  }

  const localRaw = fs.readFileSync(LOCAL_RULES_PATH, 'utf8');
  const localNorm = normalizeRules(localRaw);

  let remoteInfo;
  try {
    remoteInfo = await fetchActiveFirestoreRules();
  } catch (err) {
    console.log('COMPARE_STATUS=not_verifiable');
    console.log(`COMPARE_MESSAGE=${err.message}`);
    process.exit(2);
  }

  const remoteNorm = normalizeRules(remoteInfo.content);

  if (localNorm === remoteNorm) {
    console.log('COMPARE_STATUS=match');
    console.log(`RULESET=${remoteInfo.rulesetName}`);
    console.log(`LOCAL_BYTES=${localRaw.length}`);
    console.log(`REMOTE_BYTES=${remoteInfo.content.length}`);
    process.exit(0);
  }

  console.log('COMPARE_STATUS=diff');
  console.log(`RULESET=${remoteInfo.rulesetName}`);
  console.log('--- DIFF (primeras diferencias) ---');
  console.log(summarizeDiff(localNorm, remoteNorm));
  process.exit(1);
}

main().catch((err) => {
  console.log('COMPARE_STATUS=not_verifiable');
  console.log(`COMPARE_MESSAGE=${err.message}`);
  process.exit(2);
});
