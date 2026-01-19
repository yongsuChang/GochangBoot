import React from 'react';
import Navbar from '../components/Navbar';
import BoardList from '../components/BoardList';
import SearchBar from '../components/SearchBar';
import Pagination from '../components/Pagination';
import useBoardData from '../hooks/useBoardData';

function MainPage() {
    const {
        itemList,
        pagination,
        elementPerPage,
        handlePageClick,
        handleSearch,
        handleElementPerPageChange
    } = useBoardData();

    const handleRowClick = (id) => {
        window.location.href = `/contents/${id}`;
    };

    return (
        <div className="wrapper">
            <Navbar />

            {/* Info Start */}
            <div id="writeRow_part" className="page-header">
                <h1>전수 후기 백업 게시판</h1>
                <h5>모든 내용은 2020년 12월 31일 기준입니다.</h5>
            </div>
            {/* Info End */}

            <div className="content" style={{ padding: '20px' }}>
                <BoardList
                    itemList={itemList}
                    totalElements={pagination.total_elements}
                    onRowClick={handleRowClick}
                    elementPerPage={elementPerPage}
                    onElementPerPageChange={handleElementPerPageChange}
                />

                <SearchBar onSearch={handleSearch} />

                <Pagination
                    currentPage={pagination.current_page}
                    totalPages={pagination.total_pages}
                    onPageClick={handlePageClick}
                />
            </div>
        </div>
    );
}

export default MainPage;
