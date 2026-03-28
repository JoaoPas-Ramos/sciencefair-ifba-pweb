import { getAllPosts } from '$lib/services/post.service';
import type { PageLoad } from './$types';

export const load: PageLoad = async () => {
	const posts = await getAllPosts();

	return {
		posts
	};
};