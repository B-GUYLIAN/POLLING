import { styled } from '@mui/system';
import TablePaginationUnstyled from '@mui/base/TablePaginationUnstyled';
import Styles from "./Usesearch.module.css";
import axios from 'axios';
import { useEffect, useState } from 'react';


const Root = styled('div')`
  table {
    font-family: arial, sans-serif;
    border-collapse: collapse;
    box-shadow: 0.2vw 0.1vw 0.2vw 0.1vw #babb9536;
    width: 60vw;
    height: 40vh;
    margin-top: 20vh;
    margin-left: 20vw;
    position: fixed;
  }

  td,
  th {
    border: 1px solid #ddd;
    text-align: left;
    padding: 8px;
    height: 2vh;
    font-family: 'GangwonEdu_OTFBoldA';
    font-weight: 400;
    font-size: 2.5vh; 
  }

  th {
    background-color: #e8eca559;
    font-family: 'RussoOne';
  }
`;

const CustomTablePagination = styled(TablePaginationUnstyled)`
  & .MuiTablePaginationUnstyled-toolbar {
    display: flex;
    flex-direction: column;
    align-items: flex-start;
    gap: 10px;

    @media (min-width: 768px) {
      flex-direction: row;
      align-items: center;
    }
  }

  & .MuiTablePaginationUnstyled-selectLabel {
    margin: 0;
  }

  & .MuiTablePaginationUnstyled-displayedRows {
    margin: 0;

    @media (min-width: 768px) {
      margin-left: auto;
    }
  }

  & .MuiTablePaginationUnstyled-spacer {
    display: none;
  }

  & .MuiTablePaginationUnstyled-select {
    background-color: #e8eca000;
  }

  & .MuiTablePaginationUnstyled-actions {
    display: flex;
    gap: 0.25rem;
    background-color: #e8eca000;
  }
`;

function UserSearch2() {
  const token = sessionStorage.getItem("token")


  const [page, setPage] = useState(0);
  const [rowsPerPage, setRowsPerPage] = useState(5);
  //유저목록 받기
  const [rows, setRows] = useState([]);

  // Avoid a layout jump when reaching the last page with empty rows.
  const emptyRows =
    page > 0 ? Math.max(0, (1 + page) * rowsPerPage - rows.length) : 0;

  const handleChangePage = (event, newPage) => {
    setPage(newPage);
  };

  const handleChangeRowsPerPage = (event) => {
    setRowsPerPage(parseInt(event.target.value, 10));
    setPage(0);
  };


  const getUsers = () => {
    axios
    .get(
      `https://j6a304.p.ssafy.io/api/admin/members/0/50`,
      {
        headers: {
          "Authorization":token,
        },
      }
    )
    .then((res) => {
      setRows(res.data);
    })
    .catch(error => {
    })
  }

  useEffect(() => {

  },[])

  return (
    <Root>
      <div style={{top: '30vh'}}>
        <button onClick={getUsers} className={Styles.userlistbtn}> 회원 불러오기 </button>

        <table style={{top: '30vh'}} aria-label="custom pagination table">
          <thead>
            <tr>
              <th>Id</th>
              <th>Nickname</th>
              <th>E-mail</th>
              <th>Wallet</th>
            </tr>
          </thead>
          <tbody>
            {(rowsPerPage > 0
              ? rows.slice(page * rowsPerPage, page * rowsPerPage + rowsPerPage)
              : rows
            ).map((row, index) => (
            // {rows.map((row, index) => (
              <tr key={index}>
                <td style={{ width: '7vw' }} >{row.id}</td>
                <td style={{ width: '13vw' }} align="right">
                  {row.nickname}
                </td>
                <td style={{ width: '14vw' }} align="right">
                  {row.email}
                </td>
                <td style={{ width: '13vw' }} align="right">
                  {row.wallet}
                </td>
              </tr>
            ))
            }

            {emptyRows > 0 && (
              <tr style={{ height: 41 * emptyRows }}>
                <td colSpan={3} />
              </tr>
            )}
          </tbody>
          <tfoot>
            <tr>
              <CustomTablePagination
                rowsPerPageOptions={[5, 10, 25, { label: 'All', value: -1 }]}
                colSpan={4}
                count={rows.length}
                rowsPerPage={rowsPerPage}
                page={page}
                componentsProps={{
                  select: {
                    'aria-label': 'rows per page',
                  },
                  actions: {
                    showFirstButton: true,
                    showLastButton: true,
                  },
                }}
                onPageChange={handleChangePage}
                onRowsPerPageChange={handleChangeRowsPerPage}
              />
            </tr>
          </tfoot>
        </table>
      </div>
    </Root>
  );
}
export default UserSearch2;