import type { LayoutLoad } from './$types';

export const load: LayoutLoad = async ({ url }) => {
	return {
		searchTerm: url.searchParams.get('keyword') ?? url.searchParams.get('q') ?? ''
	};
};
