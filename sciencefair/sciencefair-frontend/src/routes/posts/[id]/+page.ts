import { getAllCategories } from '$lib/services/category.service';
import { getCommentsByPost } from '$lib/services/comment.service';
import { getPostById } from '$lib/services/post.service';
import { error } from '@sveltejs/kit';
import type { PageLoad } from './$types';

export const load: PageLoad = async ({ params }) => {
	const postId = Number(params.id);

	if (Number.isNaN(postId)) {
		throw error(404, 'Post nao encontrado');
	}

	const [post, comments, categories] = await Promise.all([
		getPostById(postId),
		getCommentsByPost(postId),
		getAllCategories()
	]);

	return {
		post,
		comments,
		categories
	};
};
