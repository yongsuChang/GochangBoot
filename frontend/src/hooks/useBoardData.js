import { useState, useEffect, useCallback } from 'react';
import axios from 'axios';

function useBoardData() {
    const [itemList, setItemList] = useState([]);
    const [pagination, setPagination] = useState({
        total_pages: 0,
        total_elements: 0,
        current_page: 0,
        current_elements: 0
    });
    const [elementPerPage, setElementPerPage] = useState(15);
    const [searchType, setSearchType] = useState('');
    const [searchWord, setSearchWord] = useState('');

    const fetchData = useCallback(async (page, size, type, word) => {
        try {
            let url = `/api/contents?page=${page}&size=${size}`;
            if (type && word) {
                url = `/api/contents/search?page=${page}&size=${size}&searchType=${type}&searchWord=${word}`;
            }

            const response = await axios.get(url);
            const data = response.data;

            setItemList(data.data);
            setPagination(data.pagination);

            console.log("Fetched data:", data);
        } catch (error) {
            console.error("Failed to fetch board data:", error);
        }
    }, []);

    useEffect(() => {
        // Initial load
        fetchData(0, elementPerPage, searchType, searchWord);
    }, [elementPerPage]); // searchType/Word trigger search explicitly, not on change

    const handlePageClick = (page) => {
        fetchData(page, elementPerPage, searchType, searchWord);
    };

    const handleSearch = (type, word) => {
        setSearchType(type);
        setSearchWord(word);
        fetchData(0, elementPerPage, type, word);
    };

    const handleElementPerPageChange = (size) => {
        setElementPerPage(size);
        // useEffect will trigger fetch
    };

    return {
        itemList,
        pagination,
        elementPerPage,
        handlePageClick,
        handleSearch,
        handleElementPerPageChange
    };
}

export default useBoardData;
