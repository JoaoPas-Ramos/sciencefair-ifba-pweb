import type { Category } from "./category"
import type { PageResponse } from "./pagination"
import type { User } from "./user"

export type Post = {
  id: number,
  title: string,
  body: string,
  creationDate: string,
  updateDate: string,
  categoryId: number,
  userId: number,
  category: Category,
  user: User
}

export type PostPayload = {
  title: string,
  body: string,
  categoryId: number,
  userId: number
}

export type PostPageResponse = PageResponse<Post>
