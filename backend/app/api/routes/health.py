"""Health and readiness check endpoints.

Provides:
- GET /health: Lightweight liveness probe verifying process is running.
- GET /health/ready: Readiness probe checking configured infrastructure reachability (PostgreSQL, Redis).
"""

import asyncio

from fastapi import APIRouter
from fastapi.responses import JSONResponse

from app.core.config import get_settings

router = APIRouter(tags=["System"])


async def _check_tcp_port(host: str, port: int, timeout: float = 1.0) -> bool:
    """Test TCP connectivity to an infrastructure host and port."""
    try:
        _, writer = await asyncio.wait_for(
            asyncio.open_connection(host, port),
            timeout=timeout,
        )
        writer.close()
        await writer.wait_closed()
        return True
    except Exception:
        return False


@router.get("/health", summary="Liveness probe")
async def health_check() -> dict:
    """Return application liveness status.

    Returns:
        Simple status response confirming the service process is alive.
    """
    return {"status": "ok"}


@router.get("/health/ready", summary="Readiness probe")
async def readiness_check() -> JSONResponse:
    """Return application readiness status checking infrastructure dependencies.

    Returns:
        Readiness state of the service and its configured dependencies (PostgreSQL, Redis).
    """
    settings = get_settings()

    postgres_ok = await _check_tcp_port(settings.postgres_host, settings.postgres_port)
    redis_ok = await _check_tcp_port(settings.redis_host, settings.redis_port)

    # In local development without Docker, if dependencies are unconfigured or down,
    # report their specific status cleanly.
    all_ready = postgres_ok and redis_ok
    status_code = 200 if all_ready else 503

    return JSONResponse(
        status_code=status_code,
        content={
            "status": "ready" if all_ready else "degraded",
            "dependencies": {
                "postgres": "connected" if postgres_ok else "unreachable",
                "redis": "connected" if redis_ok else "unreachable",
            },
        },
    )
