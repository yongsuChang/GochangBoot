import React, { useState } from 'react';

function SearchBar({ onSearch }) {
    const [searchType, setSearchType] = useState('title');
    const [searchWord, setSearchWord] = useState('');

    const handleSubmit = (e) => {
        e.preventDefault();
        onSearch(searchType, searchWord);
    };

    return (
        <div>
            <div id="searchPart" style={{ marginLeft: 'auto', marginRight: 'auto', textAlign: 'center' }}>
                <form className="form-inline mt-3 justify-content-center" onSubmit={handleSubmit}>
                    <select
                        id="searchType"
                        className="custom-select my-1 mr-sm-2"
                        style={{ width: '15%' }}
                        name="searchType"
                        value={searchType}
                        onChange={(e) => setSearchType(e.target.value)}
                    >
                        <option value="title">제목</option>
                        <option value="content">내용</option>
                        <option value="writer">작성자</option>
                    </select>
                    <input
                        id="searchWord"
                        type="text"
                        className="form-control"
                        name="searchWord"
                        placeholder="검색어를 입력하세요"
                        style={{ width: '25%' }}
                        value={searchWord}
                        onChange={(e) => setSearchWord(e.target.value)}
                    />
                    <button
                        type="submit"
                        className="btn btn-sm text-light ml-3"
                        style={{ backgroundColor: '#05445E', color: 'white' }}
                    >
                        검색
                    </button>
                </form>
            </div>
        </div>
    );
}

export default SearchBar;
