package jodconverter.proof;

import org.apache.http.client.methods.HttpPost;

interface HttpPostProvider {
    HttpPost get();
}
