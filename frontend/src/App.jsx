import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import MainPage from './pages/MainPage';

function App() {
  return (
    <Router>
      <Routes>
        <Route path="/" element={<MainPage />} />
        {/* Detail page route can be added here later */}
        {/* <Route path="/contents/:id" element={<DetailPage />} /> */}
      </Routes>
    </Router>
  );
}

export default App;
