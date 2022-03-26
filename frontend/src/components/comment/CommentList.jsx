import axios from "axios";
import styles from "./Comment.module.css"
import { useState, useEffect } from "react";
import CommentForm from "./CommentForm";



function CommentList() {


    const commentdata =[{"userName":"현우현우", "id": 1,
"content":"수지 너무 이쁘다 화이팅~!수지 너무 이쁘다 화이팅~! 수지 너무 이쁘다 화이팅~! 수지 너무 이쁘다 화이팅~! 수지 너무 이쁘다 화이팅~! "},
{"userName":"승원승원", "id": 2,
"content":"수지짱 수지짱 수지짱 수지짱 수지짱 수지짱 수지짱 수지짱 "}].reverse()
//    useEffect(()=> {
//    })

    return (

         <div className={styles.commentlist_box}>
            <ul style={{ padding:"0" }}>
             {
                 commentdata.map(comment => <CommentForm key={comment.id} id={comment.id} comment={comment}/>)
             }
         </ul>
         </div>
    );
}

export default CommentList;