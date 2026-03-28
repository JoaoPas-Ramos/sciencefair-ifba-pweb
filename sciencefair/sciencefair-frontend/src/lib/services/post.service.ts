import type { Post } from '$lib/types/post';
import { get, post, put, del } from '$lib/utils/http-utils';

export async function getAllPosts(): Promise<Post[]> {
  return await get('/posts');
}

export async function getPostById(id: number): Promise<Post> {
  return await get(`/posts/${id}`);
}

export async function createPost(payload: Partial<Post>): Promise<Post> {
  return await post('/posts', payload);
}

export async function updatePost(id: number, payload: Partial<Post>): Promise<Post> {
  return await put(`/posts/${id}`, payload);
}

export async function deletePost(id: number): Promise<void> {
  return await del(`/posts/${id}`);
}