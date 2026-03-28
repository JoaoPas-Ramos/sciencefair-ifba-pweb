import type { Category } from "$lib/types/category";
import { del, get, post, put, API_PATH } from "$lib/utils/http-utils";

export async function getAllCategories(): Promise<Category[]> {
    return await get(API_PATH.Category);
}

export async function createCategory(category: Category): Promise<Category> {
    return await post(API_PATH.Category, category, undefined, true);
}

export async function updateCategory(id: number, category: Category): Promise<Category> {
    return await put(`${API_PATH.Category}/${id}`, category, undefined, true);
}

export async function deleteCategory(id: number): Promise<void> {
    return await del(`${API_PATH.Category}/${id}`, undefined, true);
}
