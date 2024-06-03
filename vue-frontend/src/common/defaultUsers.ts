/**
 * Blogen default usernames and PWs
 * @type {Array<{user_name: string, pw: string}>}
 */

export interface DefaultUser {
  user_name: string;
  pw: string;
}

export const defaultUsers: DefaultUser[] = [
  {
    user_name: 'mcgill',
    pw: 'password'
  },
  {
    user_name: 'scotsman',
    pw: 'password'
  },
  {
    user_name: 'johndoe',
    pw: 'password'
  },
  {
    user_name: 'lizreed',
    pw: 'password'
  },
  {
    user_name: 'admin',
    pw: 'adminpassword'
  }
];
