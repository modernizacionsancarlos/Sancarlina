/** Configuración Jest para tests de Firestore Rules (emulador local). */
module.exports = {
  testEnvironment: 'node',
  testTimeout: 30000,
  testMatch: ['**/rules-tests/**/*.test.js'],
};
