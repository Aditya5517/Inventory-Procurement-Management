const API_BASE_URL =
  process.env.NEXT_PUBLIC_API_BASE_URL;

export async function apiRequest(
  endpoint: string,
  options: RequestInit = {}
) {
  const token =
    typeof window !== "undefined"
      ? localStorage.getItem("token")
      : null;

  const response = await fetch(
    `${API_BASE_URL}${endpoint}`,
    {
      ...options,

      headers: {
        "Content-Type": "application/json",

        ...(token && {
          Authorization: `Bearer ${token}`,
        }),

        ...options.headers,
      },
    }
  );

  if (!response.ok) {
    const error = await response.text();

    throw new Error(
      error || "Something went wrong"
    );
  }

  if (response.status === 204) {
    return null;
  }

  return response.json();
}