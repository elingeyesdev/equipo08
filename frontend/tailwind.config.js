/** @type {import('tailwindcss').Config} */
export default {
  darkMode: 'class',
  content: [
    "./index.html",
    "./src/**/*.{js,ts,jsx,tsx}",
  ],
  theme: {
    extend: {
      colors: {
        rose: {
          50: '#faf7f7',
          100: '#f3eaea',
          200: '#e6d3d3',
          300: '#d4b7b7',
          400: '#c09898',
          500: '#a87171',
          600: '#914e4e',
          700: '#7a2f2f',
          800: '#632525',
          900: '#522020',
          950: '#2b1010',
        }
      }
    },
  },
  corePlugins: {
    preflight: false,
  },
  plugins: [],
}
