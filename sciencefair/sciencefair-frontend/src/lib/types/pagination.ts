export type Sort = {
  empty: boolean,
  sorted: boolean,
  unsorted: boolean
}

export type Pageable = {
  pageNumber: number,
  pageSize: number,
  sort: Sort,
  offset: number,
  paged: boolean,
  unpaged: boolean
}

export type PageResponse<T> = {
  content: T[],
  pageable: Pageable,
  totalElements: number,
  totalPages: number,
  last: boolean,
  size: number,
  number: number,
  sort: Sort,
  numberOfElements: number,
  first: boolean,
  empty: boolean
}
