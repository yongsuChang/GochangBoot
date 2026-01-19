import React from 'react';

function BoardList({ itemList, totalElements, onRowClick, elementPerPage, onElementPerPageChange }) {
    return (
        <div id="contentBoard">
            <h5 id="totalElements" style={{ float: 'left' }}>총 {totalElements} 개의 글이 있습니다.</h5>
            {/* Option Start */}
            <div style={{ float: 'right' }}>
                <select id="elementPerPage" name="sel" value={elementPerPage} onChange={(e) => onElementPerPageChange(e.target.value)}>
                    <option value="10">10개씩 보기</option>
                    <option value="15">15개씩 보기</option>
                    <option value="20">20개씩 보기</option>
                    <option value="30">30개씩 보기</option>
                </select>
            </div>
            {/* Option End */}
            {/* Board Start */}
            <div id="table_part">
                <table className="table table-hover text-center">
                    <thead style={{ backgroundColor: '#a0d2ba' }}>
                        <tr role="row">
                            <th scope="col" style={{ textAlign: 'center' }}>글 번호</th>
                            <th scope="col" style={{ textAlign: 'center' }}>제목</th>
                            <th scope="col" style={{ textAlign: 'center' }}>작성자</th>
                            <th scope="col" style={{ textAlign: 'center' }}>작성일</th>
                            <th scope="col" style={{ textAlign: 'center' }}>조회수</th>
                            <th scope="col" style={{ textAlign: 'center' }}>추천수</th>
                        </tr>
                    </thead>
                    <tbody id="itemList">
                        {itemList && itemList.length > 0 ? (
                            itemList.map(dto => (
                                <tr key={dto.id} role="row" className="odd" style={{ cursor: 'pointer' }} onClick={() => onRowClick(dto.id)}>
                                    <td>{dto.id}</td>
                                    <td>{dto.title} [{dto.reply_count}]
                                        {dto.picture == '1' && <img src="/lib/img/attached.png" width="20" height="20" alt="attached" style={{ marginLeft: '5px' }} />}
                                    </td>
                                    <td>{dto.writer}</td>
                                    <td>{dto.write_date}</td>
                                    <td>{dto.count}</td>
                                    <td>{dto.recommend}</td>
                                </tr>
                            ))
                        ) : (
                            <tr>
                                <td colSpan="6">데이터가 없습니다.</td>
                            </tr>
                        )}
                    </tbody>
                </table>
            </div>
            {/* Board End */}
        </div>
    );
}
export default BoardList;
