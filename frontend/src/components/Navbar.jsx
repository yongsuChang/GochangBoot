import React from 'react';

function Navbar() {
    return (
        <nav className="navbar navbar-default navbar-fixed-top">
            <div className="container-fluid">
                <div className="navbar-header">
                    <a className="navbar-brand active" href="/">전수 후기 백업 게시판 by.목성</a>
                </div>
                <ul className="nav navbar-nav navbar-right">
                    <li><a href="/">목록으로</a></li>
                </ul>
            </div>
        </nav>
    );
}

export default Navbar;
