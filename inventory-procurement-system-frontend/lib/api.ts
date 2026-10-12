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

  async function makeRequest() {
    return fetch(`${API_BASE_URL}${endpoint}`, {
      ...options,
      headers: {
        "Content-Type": "application/json",
        ...(token && {
          Authorization: `Bearer ${token}`,
        }),
        ...options.headers,
      },
    });
  }

  let response;

  try {
    response = await makeRequest();
    if (
      response.status === 502 ||
      response.status === 503 ||
      response.status === 504
    ) {
      await new Promise((resolve) =>
        setTimeout(resolve, 3000)
      );

      response = await makeRequest();
    }
  } catch {
    await new Promise((resolve) =>
      setTimeout(resolve, 3000)
    );

    response = await makeRequest();
  }

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