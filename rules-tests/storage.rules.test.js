/**
 * Pruebas de aislamiento y tamaño para los adjuntos offline-first.
 * Se ejecutan solo contra el emulador local de Firebase Storage.
 */
const { readFileSync } = require('fs');
const path = require('path');
const {
  initializeTestEnvironment,
  assertSucceeds,
  assertFails,
} = require('@firebase/rules-unit-testing');
const {
  ref,
  uploadBytes,
  getBytes,
} = require('firebase/storage');

const PROJECT_ID = 'demo-sancarlina-storage-rules';
const RULES_PATH = path.join(__dirname, '..', 'storage.rules');
const OWNER_UID = 'owner_123';
const OTHER_UID = 'other_999';
const ADMIN_UID = 'admin_123';
const PRIVATE_PATH = `submissions/${OWNER_UID}/submission_1/photo/0_image.jpg`;

let testEnv;

function storageFor(uid, claims = {}) {
  return testEnv.authenticatedContext(uid, claims).storage();
}

beforeAll(async () => {
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    storage: {
      rules: readFileSync(RULES_PATH, 'utf8'),
    },
  });
});

afterAll(async () => {
  await testEnv.cleanup();
});

beforeEach(async () => {
  await testEnv.clearStorage();
});

test('el propietario puede subir y leer su adjunto', async () => {
  const storage = storageFor(OWNER_UID);
  const fileRef = ref(storage, PRIVATE_PATH);
  await assertSucceeds(uploadBytes(fileRef, new Uint8Array([1, 2, 3])));
  await assertSucceeds(getBytes(fileRef));
});

test('otro usuario no puede subir ni leer el adjunto', async () => {
  const otherStorage = storageFor(OTHER_UID);
  await assertFails(
    uploadBytes(ref(otherStorage, PRIVATE_PATH), new Uint8Array([1]))
  );

  await testEnv.withSecurityRulesDisabled(async (context) => {
    await uploadBytes(ref(context.storage(), PRIVATE_PATH), new Uint8Array([1]));
  });
  await assertFails(getBytes(ref(otherStorage, PRIVATE_PATH)));
});

test('un archivo mayor a 10 MB es rechazado', async () => {
  const storage = storageFor(OWNER_UID);
  const oversized = new Uint8Array(10 * 1024 * 1024 + 1);
  await assertFails(uploadBytes(ref(storage, PRIVATE_PATH), oversized));
});

test('un administrador puede leer y escribir adjuntos de soporte', async () => {
  const adminStorage = storageFor(ADMIN_UID, { role: 'admin' });
  const fileRef = ref(adminStorage, PRIVATE_PATH);
  await assertSucceeds(uploadBytes(fileRef, new Uint8Array([7, 8, 9])));
  await assertSucceeds(getBytes(fileRef));
});

test('los recursos históricos fuera de submissions conservan lectura pública', async () => {
  const publicPath = 'catalog/banner.jpg';
  await testEnv.withSecurityRulesDisabled(async (context) => {
    await uploadBytes(ref(context.storage(), publicPath), new Uint8Array([4, 5, 6]));
  });
  const unauthenticatedStorage = testEnv.unauthenticatedContext().storage();
  await assertSucceeds(getBytes(ref(unauthenticatedStorage, publicPath)));
});
