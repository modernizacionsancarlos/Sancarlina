/**
 * Tests automáticos T1–T15 para firestore.rules (emulador local).
 * Project ID ficticio: no conecta a producción.
 */
const { readFileSync } = require('fs');
const path = require('path');
const {
  initializeTestEnvironment,
  assertSucceeds,
  assertFails,
} = require('@firebase/rules-unit-testing');
const {
  doc,
  getDoc,
  setDoc,
  updateDoc,
} = require('firebase/firestore');

const PROJECT_ID = 'demo-sancarlina-rules';
const RULES_PATH = path.join(__dirname, '..', 'firestore.rules');

const TEST_UID = 'test_uid_123';
const OTHER_UID = 'otro_uid_999';
const ADMIN_UID = 'admin_uid_123';

/** Perfil ciudadano válido para create (T5). */
const VALID_PROFILE = {
  uid: TEST_UID,
  role: 'citizen',
  points: 0,
  points_balance: 0,
  user_name: 'Usuario Test',
  phone: '123',
  location: 'San Carlos',
};

/** Submission válida para create (T9). */
const VALID_SUBMISSION = {
  created_by: TEST_UID,
  form_id: 'emprendimiento',
  created_at: 1234567890,
  data: { nombre: 'Prueba' },
};

let testEnv;

/** Contexto sin autenticación. */
function unauth() {
  return testEnv.unauthenticatedContext();
}

/** Contexto autenticado con claims opcionales. */
function user(uid, claims = {}) {
  return testEnv.authenticatedContext(uid, claims);
}

/** Ciudadano autenticado sin custom claim role (hotfix T11/T12/T14). */
function citizenNoRole(uid) {
  return testEnv.authenticatedContext(uid, {});
}

/** Siembra perfil propio vía reglas (T5 payload). */
async function seedOwnProfile() {
  const db = user(TEST_UID).firestore();
  await assertSucceeds(
    setDoc(doc(db, 'userProfiles', TEST_UID), VALID_PROFILE)
  );
}

beforeAll(async () => {
  testEnv = await initializeTestEnvironment({
    projectId: PROJECT_ID,
    firestore: {
      rules: readFileSync(RULES_PATH, 'utf8'),
    },
  });
});

afterAll(async () => {
  await testEnv.cleanup();
});

beforeEach(async () => {
  await testEnv.clearFirestore();
});

describe('T1 — Catálogo público tenants', () => {
  test('get tenants/test sin auth → permitido', async () => {
    const db = unauth().firestore();
    await assertSucceeds(getDoc(doc(db, 'tenants', 'test')));
  });
});

describe('T2 — Catálogo público products', () => {
  test('get products/test sin auth → permitido', async () => {
    const db = unauth().firestore();
    await assertSucceeds(getDoc(doc(db, 'products', 'test')));
  });
});

describe('T3 — Leer perfil propio', () => {
  test('get userProfiles/test_uid_123 como dueño → permitido', async () => {
    const db = user(TEST_UID).firestore();
    await assertSucceeds(getDoc(doc(db, 'userProfiles', TEST_UID)));
  });
});

describe('T4 — Leer perfil ajeno', () => {
  test('get userProfiles/otro_uid_999 como test_uid_123 → denegado', async () => {
    const db = user(TEST_UID).firestore();
    await assertFails(getDoc(doc(db, 'userProfiles', OTHER_UID)));
  });
});

describe('T5 — Crear perfil propio válido', () => {
  test('set userProfiles/test_uid_123 con datos válidos → permitido', async () => {
    const db = user(TEST_UID).firestore();
    await assertSucceeds(
      setDoc(doc(db, 'userProfiles', TEST_UID), VALID_PROFILE)
    );
  });
});

describe('T6 — Actualizar perfil propio con campos seguros', () => {
  beforeEach(async () => {
    await seedOwnProfile();
  });

  test('update user_name, phone, location → permitido', async () => {
    const db = user(TEST_UID).firestore();
    await assertSucceeds(
      updateDoc(doc(db, 'userProfiles', TEST_UID), {
        user_name: 'Nuevo Nombre',
        phone: '456',
        location: 'Mendoza',
      })
    );
  });
});

describe('T7 — Intentar cambiar points', () => {
  beforeEach(async () => {
    await seedOwnProfile();
  });

  test('update points a 9999 → denegado', async () => {
    const db = user(TEST_UID).firestore();
    await assertFails(
      updateDoc(doc(db, 'userProfiles', TEST_UID), { points: 9999 })
    );
  });
});

describe('T8 — Intentar cambiar role', () => {
  beforeEach(async () => {
    await seedOwnProfile();
  });

  test('update role a admin → denegado', async () => {
    const db = user(TEST_UID).firestore();
    await assertFails(
      updateDoc(doc(db, 'userProfiles', TEST_UID), { role: 'admin' })
    );
  });
});

describe('T9 — Crear submission propia', () => {
  test('create Submissions/test_submission_1 → permitido', async () => {
    const db = user(TEST_UID).firestore();
    await assertSucceeds(
      setDoc(doc(db, 'Submissions', 'test_submission_1'), VALID_SUBMISSION)
    );
  });
});

describe('T10 — Crear submission con otro UID', () => {
  test('create Submissions con created_by ajeno → denegado', async () => {
    const db = user(TEST_UID).firestore();
    await assertFails(
      setDoc(doc(db, 'Submissions', 'test_submission_2'), {
        ...VALID_SUBMISSION,
        created_by: OTHER_UID,
      })
    );
  });
});

describe('T11 — Leer Submissions como ciudadano sin role', () => {
  beforeEach(async () => {
    // Semilla sin reglas: solo admin puede escribir Submissions en producción.
    await testEnv.withSecurityRulesDisabled(async (context) => {
      await setDoc(
        doc(context.firestore(), 'Submissions', 'test_submission_1'),
        VALID_SUBMISSION
      );
    });
  });

  test('get Submissions sin claim role → denegado sin error de evaluación', async () => {
    const db = citizenNoRole(TEST_UID).firestore();
    await assertFails(getDoc(doc(db, 'Submissions', 'test_submission_1')));
  });
});

describe('T12 — Escribir categoría como ciudadano sin role', () => {
  test('create categories sin claim role → denegado sin error de evaluación', async () => {
    const db = citizenNoRole(TEST_UID).firestore();
    await assertFails(
      setDoc(doc(db, 'categories', 'test_category'), { name: 'Categoría falsa' })
    );
  });
});

describe('T13 — Escribir categoría como admin', () => {
  test('create categories con role admin → permitido', async () => {
    const db = user(ADMIN_UID, { role: 'admin' }).firestore();
    await assertSucceeds(
      setDoc(doc(db, 'categories', 'test_category_admin'), {
        name: 'Categoría admin',
      })
    );
  });
});

describe('T14 — Leer AuditLogs como ciudadano sin role', () => {
  test('get AuditLogs sin claim role → denegado sin error', async () => {
    const db = citizenNoRole(TEST_UID).firestore();
    await assertFails(getDoc(doc(db, 'AuditLogs', 'test_log')));
  });
});

describe('T15 — Catch-all deny', () => {
  test('get unknownCollection/test sin auth → denegado', async () => {
    const db = unauth().firestore();
    await assertFails(getDoc(doc(db, 'unknownCollection', 'test')));
  });
});
