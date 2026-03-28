import type { User } from "./user"

export type Comment = {
    id?: number,
    body: string,
    rating: number,
    postId: number,
    userId: number,
    user?: User
}
