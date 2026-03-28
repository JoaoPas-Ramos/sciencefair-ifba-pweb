import type { ErrorResponse } from "$lib/types/error-response";
import { getAuthToken } from "$lib/utils/auth";

const API_BASEPATH = 'http://localhost:8080';

export const API_PATH = {
    Login: '/login',
    Category: '/categories',
    Post: '/posts',
    Comment: '/comments',
    User: '/users'
} as const;

export class ApiError extends Error {
    status: number;
    body?: ErrorResponse;

    constructor(status: number, body?: ErrorResponse) {
        super(body?.description ?? `Request failed with status ${status}`);
        this.name = 'ApiError';
        this.status = status;
        this.body = body;
    }
}

export async function get<T>(
    path: string,
    queryParams?: {},
    authenticated?: boolean,
    customHeaders?: HeadersInit
): Promise<T> {
    const url = buildUrl(path, queryParams);

    const response = await fetch(url, {
        method: 'GET',
        headers: buildHeaders(authenticated, customHeaders)
    })

    return handleResponse<T>(response);
}

export async function post<TResponse, TBody>(
    path: string,
    body?: TBody,
    queryParams?: {},
    authenticated?: boolean,
    customHeaders?: HeadersInit
): Promise<TResponse> {
    const url = buildUrl(path, queryParams);
    const hasBody = body !== undefined;

    const response = await fetch(url, {
        method: 'POST',
        body: hasBody ? JSON.stringify(body) : undefined,
        headers: buildHeaders(authenticated, customHeaders, hasBody)
    })

    return handleResponse<TResponse>(response);
}

export async function put<TResponse, TBody>(
    path: string,
    body?: TBody,
    queryParams?: {},
    authenticated?: boolean,
    customHeaders?: HeadersInit
): Promise<TResponse> {
    const url = buildUrl(path, queryParams);
    const hasBody = body !== undefined;

    const response = await fetch(url, {
        method: 'PUT',
        body: hasBody ? JSON.stringify(body) : undefined,
        headers: buildHeaders(authenticated, customHeaders, hasBody)
    })

    return handleResponse<TResponse>(response);
}

export async function del<T>(
    path: string,
    queryParams?: {},
    authenticated?: boolean,
    customHeaders?: HeadersInit
): Promise<T> {
    const url = buildUrl(path, queryParams);

    const response = await fetch(url, {
        method: 'DELETE',
        headers: buildHeaders(authenticated, customHeaders)
    })

    return handleResponse<T>(response);
}

function buildUrl(path: string, queryParams?: {}): URL {
    const url  = new URL(path, API_BASEPATH);

    if (queryParams) {
        Object.entries(queryParams).forEach(([key, value]) => {
            if (Array.isArray(value)) {
                value.forEach((v) => {
                    if (v !== undefined && v !== null && v !== '') {
                        url.searchParams.append(key, String(v));
                    }
                })
            } else if (value !== undefined && value !== null && value !== '') {
                url.searchParams.append(key, String(value));
            }
        });
    }

    return url;
}

function buildHeaders(
    authenticated?: boolean,
    customHeaders?: HeadersInit,
    hasJsonBody = false
): HeadersInit {
    const headers: Record<string, string> = {};

    if (hasJsonBody) {
        headers['Content-Type'] = 'application/json';
    }

    if (authenticated) {
        const authorization = getAuthToken();

        if (authorization) {
            headers.Authorization = authorization;
        }
    }

    if (customHeaders) {
        Object.assign(headers, Object.fromEntries(new Headers(customHeaders).entries()));
    }

    return headers;
}

async function handleResponse<T>(response: Response): Promise<T> {
    if (!response.ok) {
        const contentType = response.headers.get('content-type') ?? '';
        let body: ErrorResponse | undefined;

        if (contentType.includes('application/json')) {
            body = await response.json() as ErrorResponse;
        }

        throw new ApiError(response.status, body);
    }

    if (response.status === 204) {
        return null as T;
    }

    return response.json() as Promise<T>;
}
