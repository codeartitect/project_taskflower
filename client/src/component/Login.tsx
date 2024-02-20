import {useEffect, useState} from "react";
import axios from "axios";

export const Login: React.FC = () => {
  const [email, setEmail] = useState('');
  const [password, setPassword] = useState('');

  const fetchLogin = async () => {
    const requestOptions = {
      method: "GET",
      headers: {
        "Content-Type": "application/json"
      },
      // body: JSON.stringify({
      //   email: "test@test.test",
      //   password: "1234"
      // }),
    }
    try {
      await fetch("http://localhost:8080/api/v1/auth/login", requestOptions).then(res => {
        res.json().then(result => {
          console.log(result);
        })
      })
    } catch (e) {
      console.log(e);
    }

  }

  const handleLogin = async (event: React.FormEvent) => {
    event.preventDefault(); // 폼 제출 이벤트의 기본 동작 중지
    await fetchLogin();
  };



  return (
    <div className="flex items-center justify-center h-screen bg-blue-50">
      <div className="bg-white p-8 rounded-lg shadow-md w-96">
        <h2 className="text-3xl font-semibold text-center mb-4">Login</h2>
        <form onSubmit={handleLogin}>
          <div className="mb-4">
            <label htmlFor="email" className="block text-sm font-medium text-gray-700">
              Email address
            </label>
            <input
              type="email"
              id="email"
              className="mt-1 p-2 w-full rounded border-gray-300 focus:border-blue-500 focus:ring focus:ring-blue-500"
              placeholder="Enter your email"
              value={email}
              autoComplete={"current-email"}
              onChange={e => setEmail(e.target.value)}
            />
          </div>
          <div className="mb-4">
            <label htmlFor="password" className="block text-sm font-medium text-gray-700">
              Password
            </label>
            <input
              type="password"
              id="password"
              className="mt-1 p-2 w-full rounded border-gray-300 focus:border-blue-500 focus:ring focus:ring-blue-500"
              placeholder="Enter your password"
              value={password}
              autoComplete={"current-password"}
              onChange={e => setPassword(e.target.value)}
            />
          </div>
          <div className="flex items-center justify-between">
            <div className="flex items-center">
              <input
                type="checkbox"
                id="remember"
                className="rounded border-gray-300 text-blue-500 focus:border-blue-500 focus:ring focus:ring-blue-500"
              />
              <label htmlFor="remember" className="ml-2 block text-sm text-gray-700">
                Remember me
              </label>
            </div>
            <a href="#" className="text-sm text-blue-500 hover:underline">
              Forgot your password?
            </a>
          </div>
          <button
            type="submit"
            className="mt-4 w-full px-4 py-2 bg-blue-500 text-white rounded hover:bg-blue-600 focus:outline-none focus:bg-blue-600"
            onClick={handleLogin}
          >
            Login
          </button>
        </form>
      </div>
    </div>
  );
}
