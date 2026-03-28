import { getAllCategories } from '$lib/services/category.service';
import type { PageLoad } from './$types';

export const load: PageLoad = async () => {
	return {
		categories: await getAllCategories()
	};
};
