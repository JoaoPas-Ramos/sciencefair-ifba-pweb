import type { Comment } from "$lib/types/comment";
import { API_PATH, del, get, post, put } from "$lib/utils/http-utils";

export async function createComment(comment: Comment): Promise<Comment> {
  return await post(API_PATH.Comment, comment, undefined, true);
}

export async function updateComment(id: number, comment: Comment): Promise<Comment> {
  return await put(`${API_PATH.Comment}/${id}`, comment, undefined, true);
}

export async function deleteComment(id: number): Promise<void> {
  return await del(`${API_PATH.Comment}/${id}`, undefined, true);
}

export async function getCommentsByPost(postId: number): Promise<Comment[]> {
  return await get(API_PATH.Comment, { postId });
}
