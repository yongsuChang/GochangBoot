import React, { useEffect, useState } from 'react';

function Pagination({ currentPage, totalPages, maxBtnSize = 10, onPageClick }) {
    const [btnList, setBtnList] = useState([]);

    useEffect(() => {
        const temp = Math.floor(currentPage / maxBtnSize);
        const list = [];
        for (let i = 1; i <= maxBtnSize; i++) {
            const value = i + (temp * maxBtnSize);
            if (value <= totalPages) {
                list.push(value);
            }
        }
        setBtnList(list);
    }, [currentPage, totalPages, maxBtnSize]);

    const handlePrevious = () => {
        if (currentPage > 0) {
            onPageClick(currentPage - 1);
        }
    };

    const handleNext = () => {
        if (currentPage < totalPages - 1) {
            onPageClick(currentPage + 1);
        }
    };

    const handleFirst = () => {
        if (currentPage > 0) {
            onPageClick(0);
        }
    };

    const handleLast = () => {
        if (currentPage < totalPages - 1) {
            onPageClick(totalPages - 1);
        }
    };

    return (
        <div id="pagingPart">
            <div style={{ display: 'block', textAlign: 'center' }}>
                <div className="dataTables_paginate paging_simple_numbers" id="pageBtn">
                    <ul className="pagination">
                        <li className={`paginate_button ${currentPage === 0 ? 'disabled' : ''}`} id="firstPageBtn">
                            <a href="#!" onClick={(e) => { e.preventDefault(); handleFirst(); }}>맨앞</a>
                        </li>
                        <li className={`paginate_button previous ${currentPage === 0 ? 'disabled' : ''}`} id="previousBtn">
                            <a href="#!" onClick={(e) => { e.preventDefault(); handlePrevious(); }}>이전</a>
                        </li>

                        {btnList.map(btn => (
                            <li key={btn} className={`paginate_button ${currentPage + 1 === btn ? 'active' : ''}`} btn_id={btn}>
                                <a href="#!" onClick={(e) => { e.preventDefault(); onPageClick(btn - 1); }}>{btn}</a>
                            </li>
                        ))}

                        <li className={`paginate_button next ${currentPage === totalPages - 1 ? 'disabled' : ''}`} id="nextBtn">
                            <a href="#!" onClick={(e) => { e.preventDefault(); handleNext(); }}>다음</a>
                        </li>
                        <li className={`paginate_button ${currentPage === totalPages - 1 ? 'disabled' : ''}`} id="lastPageBtn">
                            <a href="#!" onClick={(e) => { e.preventDefault(); handleLast(); }}>맨뒤</a>
                        </li>
                    </ul>
                </div>
            </div>
        </div>
    );
}

export default Pagination;
